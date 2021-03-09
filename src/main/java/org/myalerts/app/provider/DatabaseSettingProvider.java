package org.myalerts.app.provider;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jooq.lambda.Unchecked;
import org.myalerts.app.model.Setting;
import org.myalerts.app.repository.SettingRepository;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.myalerts.app.provider.SettingProvider.BOOLEAN_TYPE;
import static org.myalerts.app.provider.SettingProvider.INTEGER_TYPE;
import static org.myalerts.app.provider.SettingProvider.PASSWORD_TYPE;
import static org.myalerts.app.provider.SettingProvider.STRING_TYPE;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public final class DatabaseSettingProvider implements InvocationHandler {

    private final Object lock = new Object();

    private final DefaultSettingProvider defaultSettingProvider;

    private final SettingRepository settingRepository;

    private final List<Setting> availableSettings;

    public DatabaseSettingProvider(@NonNull DefaultSettingProvider defaultSettingProvider, @NonNull SettingRepository settingRepository) {
        this.defaultSettingProvider = defaultSettingProvider;
        this.settingRepository = settingRepository;
        this.availableSettings = settingRepository.findAllByOrderBySequence().stream()
            .map(setting -> Pair.of(setting, transformTo(setting.getType(), setting.getValue())))
            .filter(pair -> pair.getRight().isPresent())
            .map(pair -> {
                pair.getLeft().setComputedValue(pair.getRight().get());
                return pair.getLeft();
            })
            .collect(Collectors.toList());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (isGetOrDefault(method)) {
            return getSetting(method, args);
        } else if (isGetAll(method)) {
            return List.copyOf(availableSettings);
        } else if (isSet(method)) {
            return setSetting(args);
        } else {
            return method.invoke(defaultSettingProvider, args);
        }
    }

    private Optional<Object> transformTo(String type, String value) {
        switch (type) {
            case STRING_TYPE:
            case PASSWORD_TYPE:
                return of(value);
            case INTEGER_TYPE:
                return of(Integer.parseInt(value));
            case BOOLEAN_TYPE:
                return of(Boolean.parseBoolean(value));
            default:
                return empty();
        }
    }

    private boolean isGetOrDefault(Method method) {
        return "getOrDefault".equals(method.getName());
    }

    private boolean isGetAll(Method method) {
        return "getAll".equals(method.getName());
    }

    private boolean isSet(Method method) {
        return "set".equals(method.getName());
    }

    private Object getSetting(Method method, Object[] args) throws Throwable {
        if (args.length < 1 || !(args[0] instanceof Setting.Key)) {
            return method.invoke(defaultSettingProvider, args);
        }

        return availableSettings.stream()
            .filter(setting -> setting.getKey().equals(((Setting.Key) args[0]).getKey()))
            .findFirst()
            .map(Setting::getComputedValue)
            .orElseGet(() -> Unchecked.supplier(() -> method.invoke(defaultSettingProvider, args)).get());
    }

    private Object setSetting(Object[] args) {
        if (args.length != 2 || !(args[0] instanceof Setting.Key)) {
            return null;
        }

        synchronized (lock) {
            availableSettings.stream()
                .filter(setting -> setting.getKey().equals(((Setting.Key) args[0]).getKey()))
                .filter(setting -> !setting.getComputedValue().equals(args[1]))
                .findFirst()
                .ifPresent(setting -> {
                    final Object newValue = args[1];
                    setting.setComputedValue(newValue);
                    setting.setValue(newValue + StringUtils.EMPTY);
                    settingRepository.save(setting);
                });
        }

        return null;
    }

}
