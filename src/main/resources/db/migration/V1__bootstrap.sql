CREATE TABLE menu_items (
    label TEXT PRIMARY KEY NOT NULL,
    icon TEXT NOT NULL,
    target TEXT NOT NULL,
    role TEXT DEFAULT guest,
    position INTEGER NOT NULL DEFAULT (0)
);

INSERT INTO menu_items ("label", "icon", "target", "role", "position")
VALUES ('menu.main.test-scenarios', 'la la-globe', 'org.myalerts.view.TestScenarioView', 'ROLE_GUEST', '1');
INSERT INTO menu_items ("label", "icon", "target", "role", "position")
VALUES ('menu.main.settings', 'la la-cog', 'org.myalerts.view.SettingsView', 'ROLE_ADMIN', '2');
INSERT INTO menu_items ("label", "icon", "target", "role", "position")
VALUES ('menu.main.statistics', 'la la-tachometer-alt', 'org.myalerts.view.StatisticsView', 'ROLE_GUEST', '3');
INSERT INTO menu_items ("label", "icon", "target", "role", "position")
VALUES ('menu.main.about', 'la la-heart', 'org.myalerts.view.AboutView', 'ROLE_GUEST', '4');
INSERT INTO menu_items ("label", "icon", "target", "role", "position")
VALUES ('menu.main.login', 'la la-user', 'org.myalerts.view.LoginView', 'ROLE_NOT_LOGGED', '5');
INSERT INTO menu_items ("label", "icon", "target", "role", "position")
VALUES ('menu.main.logout', 'la la-user', 'org.myalerts.view.LogoutView', 'ROLE_LOGGED', '6');

CREATE TABLE scenarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    enabled BOOLEAN NOT NULL DEFAULT (1),
    name TEXT NOT NULL UNIQUE,
    cron TEXT NOT NULL,
    definition TEXT NOT NULL DEFAULT "function run(secondsSinceLatestRun) {}"
);

INSERT INTO scenarios ("enabled", "name", "cron", "definition")
VALUES ('1', 'Passing test scenario', '0 0 * * MON-FRI', "function run(secondsSinceLatestRun) {
}");
INSERT INTO scenarios ("enabled", "name", "cron", "definition")
VALUES ('1', 'Failing test scenario', '0 3 * * MON-FRI', "function run(secondsSinceLatestRun) {
    return 'This scenario fails every time';
}");
INSERT INTO scenarios ("enabled", "name", "cron", "definition")
VALUES ('1', 'Status check test scenario', '0 6 * * MON-FRI', "function run(secondsSinceLatestRun) {
    var HttpRequestHelper = Java.type('org.myalerts.helper.HttpRequestHelper');
    var HttpResponse = new HttpRequestHelper()
        .http2()
        .requestUri('https://aventurata.ro')
        .sendGet();
    if (HttpResponse.statusCode() !== 200) {
        return 'Service is not returning 200 as status code.';
    }
}");
INSERT INTO scenarios ("enabled", "name", "cron", "definition")
VALUES ('1', 'Body check test scenario', '0 9 * * MON-FRI', "function run(secondsSinceLatestRun) {
    var HttpRequestHelper = Java.type('org.myalerts.helper.HttpRequestHelper');
    var HttpResponse = new HttpRequestHelper()
        .http2()
        .requestUri('https://mihaisurdeanu.ro/wp-json/')
        .sendGet();
    if (HttpResponse.statusCode() !== 200) {
        return 'Service is not returning 200 as status code.';
    }
    const JsonResponse = JSON.parse(HttpResponse.body());
    if (JsonResponse.name !== 'Mihai Surdeanu') {
        return 'Name is not the one expected.';
    }
}");

CREATE TABLE results (
    id INTEGER PRIMARY KEY NOT NULL,
    scenario_id INTEGER NOT NULL,
    duration INTEGER NOT NULL DEFAULT (0),
    cause TEXT,
    created DATETIME NOT NULL
);

CREATE TABLE settings (
    "key" TEXT PRIMARY KEY NOT NULL,
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    type TEXT NOT NULL,
    value TEXT NOT NULL,
    editable INTEGER NOT NULL DEFAULT 1,
    position INTEGER NOT NULL DEFAULT 0
);

INSERT INTO settings ("key", "title", "description", "type", "value", "editable", "position")
VALUES ('language', 'settings.language.label', 'settings.language.helper', 'text', 'ro', '1', '1');
INSERT INTO settings ("key", "title", "description", "type", "value", "editable", "position")
VALUES ('gridPageSize', 'settings.grid.page-size.label', 'settings.grid.page-size.helper', 'int', '15', '1', '2');
INSERT INTO settings ("key", "title", "description", "type", "value", "editable", "position")
VALUES ('gridPaginatorSize', 'settings.grid.paginator-size.label', 'settings.grid.paginator-size.helper', 'int', '5', '1', '3');
INSERT INTO settings ("key", "title", "description", "type", "value", "editable", "position")
VALUES ('testScenarioExecTimeout', 'settings.test-scenario.exec-timeout.label', 'settings.test-scenario.exec-timeout.helper', 'int', '60', '1', '4');
INSERT INTO settings ("key", "title", "description", "type", "value", "editable", "position")
VALUES ('testScenarioPoolSize', 'settings.test-scenario.pool-size.label', 'settings.test-scenario.pool-size.helper', 'int', '1', '1', '5');
INSERT INTO settings ("key", "title", "description", "type", "value", "editable", "position")
VALUES ('testScenarioThreadNamePrefix', 'settings.test-scenario.thread-name-prefix.label', 'settings.test-scenario.thread-name-prefix.helper', 'text', 'test-scenario-pool-', '0', '6');
INSERT INTO settings ("key", "title", "description", "type", "value", "editable", "position")
VALUES ('cacheMenuItemMaxSize', 'settings.cache.menu-item.max-size.label', 'settings.cache.menu-item.max-size.helper', 'int', '20', '0', '7');
INSERT INTO settings ("key", "title", "description", "type", "value", "editable", "position")
VALUES ('cacheMenuItemExpireAfterAccess', 'settings.cache.menu-item.expire-after-access.label', 'settings.cache.menu-item.expire-after-access.helper', 'int', '0', '0', '8');
INSERT INTO settings ("key", "title", "description", "type", "value", "editable", "position")
VALUES ('cacheMenuItemExpireAfterWrite', 'settings.cache.menu-item.expire-after-write.label', 'settings.cache.menu-item.expire-after-write.helper', 'int', '86400', '0', '9');
INSERT INTO settings ("key", "title", "description", "type", "value", "editable", "position")
VALUES ('cacheTestScenarioResultMaxSize', 'settings.cache.test-scenario-result.max-size.label', 'settings.cache.test-scenario-result.max-size.helper', 'int', '100', '0', '10');
INSERT INTO settings ("key", "title", "description", "type", "value", "editable", "position")
VALUES ('cacheTestScenarioResultExpireAfterAccess', 'settings.cache.test-scenario-result.expire-after-access.label', 'settings.cache.test-scenario-result.expire-after-access.helper', 'int', '3600', '0', '11');
INSERT INTO settings ("key", "title", "description", "type", "value", "editable", "position")
VALUES ('cacheTestScenarioResultExpireAfterWrite', 'settings.cache.test-scenario-result.expire-after-write.label', 'settings.cache.test-scenario-result.expire-after-write.helper', 'int', '0', '0', '12');

CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    enabled INTEGER (1) NOT NULL DEFAULT (1),
    username TEXT NOT NULL,
    password TEXT NOT NULL,
    email TEXT NOT NULL,
    role TEXT DEFAULT 'user'
);

INSERT INTO users ("username", "password", "email", "role")
VALUES ('test', '$2a$10$iZNmfanuP/Pn8OntMEsEKe6nH5JzL650v3zZM4aFNw4D36Wbq8ofG', 'test@myalerts.org', 'ROLE_USER');
INSERT INTO users ("username", "password", "email", "role")
VALUES ('admin', '$2a$10$18ldMrqn.vZIPvPUemB40eR5OTXIjLOAVrdFQdGCF6Bmh1l4ceYH2', 'admin@myalerts.org', 'ROLE_ADMIN');

CREATE UNIQUE INDEX key_index ON settings ("key");
CREATE UNIQUE INDEX username_index ON users (username);

CREATE TRIGGER onScenarioDelete AFTER DELETE ON scenarios FOR EACH ROW BEGIN
    DELETE FROM results WHERE scenario_id = id;
END;
