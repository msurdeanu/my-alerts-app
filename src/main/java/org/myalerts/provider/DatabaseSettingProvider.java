package org.myalerts.provider;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jooq.lambda.Unchecked;

import org.myalerts.mapper.Mapper1;
import org.myalerts.model.Setting;
import org.myalerts.model.SettingType;
import org.myalerts.repository.SettingRepository;

import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Slf4j
public final class DatabaseSettingProvider implements InvocationHandler {

    private static final Mapper1<SettingType, String, Optional<Object>> SETTING_TO_OBJECT_MAPPING = createMapper();

    private final Lock lock = new ReentrantLock();

    private final DefaultSettingProvider defaultSettingProvider;

    private final SettingRepository settingRepository;

    private final List<Setting> availableSettings;

    public DatabaseSettingProvider(@NonNull final DefaultSettingProvider defaultSettingProvider,
                                   @NonNull final SettingRepository settingRepository) {
        this.defaultSettingProvider = defaultSettingProvider;
        this.settingRepository = settingRepository;
        this.availableSettings = settingRepository.findAllByOrderByPosition().stream()
            .map(setting -> Pair.of(setting, transformTo(setting.getType(), setting.getValue())))
            .filter(pair -> pair.getRight().isPresent())
            .map(pair -> {
                pair.getLeft().setComputedValue(pair.getRight().get());
                return pair.getLeft();
            })
            .collect(Collectors.toList());
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
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

    private Optional<Object> transformTo(final SettingType type, final String value) {
        return SETTING_TO_OBJECT_MAPPING.map(type, value);
    }

    private boolean isGetOrDefault(final Method method) {
        return "getOrDefault".equals(method.getName());
    }

    private boolean isGetAll(final Method method) {
        return "getAll".equals(method.getName());
    }

    private boolean isSet(final Method method) {
        return "set".equals(method.getName());
    }

    private Object getSetting(final Method method, final Object[] args) throws Throwable {
        if (args.length < 1 || !(args[0] instanceof Setting.Key)) {
            return method.invoke(defaultSettingProvider, args);
        }

        return availableSettings.stream()
            .filter(setting -> setting.getKey().equals(((Setting.Key) args[0]).getKey()))
            .findFirst()
            .map(Setting::getComputedValue)
            .orElseGet(() -> Unchecked.supplier(() -> method.invoke(defaultSettingProvider, args)).get());
    }

    private Object setSetting(final Object[] args) {
        if (args.length != 2 || !(args[0] instanceof Setting.Key)) {
            return null;
        }

        lock.lock();
        try {
            availableSettings.stream()
                .filter(setting -> setting.getKey().equals(((Setting.Key) args[0]).getKey()))
                .filter(setting -> !setting.getComputedValue().equals(args[1]))
                .findFirst()
                .ifPresent(setting -> setSettingValue(setting, args[1]));
        } finally {
            lock.unlock();
        }

        return null;
    }

    private void setSettingValue(final Setting setting, final Object value) {
        setting.setComputedValue(value);
        setting.setValue(value + StringUtils.EMPTY);
        settingRepository.save(setting);
    }

}
