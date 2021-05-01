package org.myalerts.app.provider;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jooq.lambda.Unchecked;

import org.myalerts.app.mapper.Mapper1;
import org.myalerts.app.model.Setting;
import org.myalerts.app.model.SettingType;
import org.myalerts.app.repository.SettingRepository;

import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public final class DatabaseSettingProvider implements InvocationHandler {

    private static final Mapper1<SettingType, String, Optional<Object>> SETTING_TO_OBJECT_MAPPING = createMapper();

    private final Lock lock = new ReentrantLock();

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

    private static Mapper1<SettingType, String, Optional<Object>> createMapper() {
        return Mapper1.<SettingType, String, Optional<Object>>builder(new EnumMap<>(SettingType.class))
            .map(SettingType.TEXT, Optional::of)
            .map(SettingType.PASSWORD, Optional::of)
            .map(SettingType.INTEGER, value -> of(Integer.parseInt(value)))
            .map(SettingType.BOOLEAN, value -> of(Boolean.parseBoolean(value)))
            .unmapped(value -> empty())
            .build();
    }

    private Optional<Object> transformTo(SettingType type, String value) {
        return SETTING_TO_OBJECT_MAPPING.map(type, value);
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

        lock.lock();
        try {
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
        } finally {
            lock.unlock();
        }

        return null;
    }

}
