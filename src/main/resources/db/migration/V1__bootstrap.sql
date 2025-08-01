CREATE TABLE menu_items
(
    label    TEXT PRIMARY KEY NOT NULL,
    icon     TEXT             NOT NULL,
    target   TEXT             NOT NULL,
    role     TEXT                      DEFAULT 'ROLE_GUEST',
    position INTEGER          NOT NULL DEFAULT (0)
);

INSERT INTO menu_items ("label", "icon", "target", "role", "position")
VALUES ('menu.main.test-scenarios', 'globe', 'org.myalerts.view.TestScenarioView', 'ROLE_GUEST', '1');
INSERT INTO menu_items ("label", "icon", "target", "role", "position")
VALUES ('menu.main.plugins', 'plug', 'org.myalerts.view.PluginView', 'ROLE_ADMIN', '2');
INSERT INTO menu_items ("label", "icon", "target", "role", "position")
VALUES ('menu.main.settings', 'cog', 'org.myalerts.view.SettingView', 'ROLE_ADMIN', '3');
INSERT INTO menu_items ("label", "icon", "target", "role", "position")
VALUES ('menu.main.statistics', 'chart', 'org.myalerts.view.StatisticView', 'ROLE_GUEST', '4');
INSERT INTO menu_items ("label", "icon", "target", "role", "position")
VALUES ('menu.main.about', 'heart', 'org.myalerts.view.AboutView', 'ROLE_GUEST', '5');
INSERT INTO menu_items ("label", "icon", "target", "role", "position")
VALUES ('menu.main.login', 'user', 'org.myalerts.view.LoginView', 'ROLE_NOT_LOGGED', '6');

CREATE TABLE scenarios
(
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    enabled    BOOLEAN NOT NULL DEFAULT (1),
    name       TEXT    NOT NULL UNIQUE,
    cron       TEXT    NOT NULL,
    definition TEXT    NOT NULL DEFAULT "def run(context) {}"
);

INSERT INTO scenarios ("enabled", "name", "cron", "definition")
VALUES ('1', 'Passing test scenario', '0 0 * * MON-FRI', "def run(context) {
}");
INSERT INTO scenarios ("enabled", "name", "cron", "definition")
VALUES ('1', 'Failing test scenario', '0 3 * * MON-FRI', "def run(context) {
    context.markAsFailed('This scenario fails every time with this cause');
}");
INSERT INTO scenarios ("enabled", "name", "cron", "definition")
VALUES ('1', 'Status check test scenario', '0 6 * * MON-FRI', "def run(context) {
    def httpResponse = httpRequest
        .http2()
        .requestUri('https://aventurata.ro')
        .sendGet();
    assert 200 == httpResponse.statusCode() : 'Unexpected status code received';
}");
INSERT INTO scenarios ("enabled", "name", "cron", "definition")
VALUES ('1', 'Body check test scenario', '0 9 * * MON-FRI', "import groovy.json.JsonSlurper;
def run(context) {
    def httpResponse = httpRequest
        .http2()
        .requestUri('https://aventurata.ro/wp-json/')
        .sendGet();
    assert 200 == httpResponse.statusCode() : 'Unexpected status code received';
    def jsonResponse = new JsonSlurper().parseText(httpResponse.body());
    assert 'Mihai Surdeanu' == jsonResponse.name : 'Unexpected name received';
}");

CREATE TABLE results
(
    id          INTEGER PRIMARY KEY NOT NULL,
    scenario_id INTEGER             NOT NULL,
    duration    INTEGER             NOT NULL DEFAULT (0),
    cause       TEXT,
    created     DATETIME            NOT NULL
);

CREATE TABLE tags
(
    id   INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT
);

INSERT INTO tags ("name")
VALUES ('simple');

CREATE UNIQUE INDEX tags_name_index ON tags (name);

CREATE TABLE scenarios_tags
(
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    scenario_id INTEGER NOT NULL,
    tag_id      INTEGER NOT NULL
);

INSERT INTO scenarios_tags ("scenario_id", "tag_id")
VALUES (1, 1);

CREATE TABLE settings
(
    "key"       TEXT PRIMARY KEY NOT NULL,
    title       TEXT             NOT NULL,
    description TEXT             NOT NULL,
    type        TEXT             NOT NULL,
    value       TEXT             NOT NULL,
    editable    INTEGER          NOT NULL DEFAULT 1,
    position    INTEGER          NOT NULL DEFAULT 0
);

INSERT INTO settings ("key", "title", "description", "type", "value", "editable", "position")
VALUES ('version', '', '', 'text_h', '1.0', '0', '0');
INSERT INTO settings ("key", "title", "description", "type", "value", "editable", "position")
VALUES ('language', 'settings.language.label', 'settings.language.helper', 'text', 'en', '1', '1');
INSERT INTO settings ("key", "title", "description", "type", "value", "editable", "position")
VALUES ('salt', 'settings.salt.label', 'settings.salt.helper', 'text', lower(hex(randomblob(16))), '1', '2');
INSERT INTO settings ("key", "title", "description", "type", "value", "editable", "position")
VALUES ('testScenarioExecTimeout', 'settings.test-scenario.exec-timeout.label',
        'settings.test-scenario.exec-timeout.helper', 'int', '60', '1', '3');
INSERT INTO settings ("key", "title", "description", "type", "value", "editable", "position")
VALUES ('cacheMenuItemMaxSize', 'settings.cache.menu-item.max-size.label', 'settings.cache.menu-item.max-size.helper',
        'int', '20', '0', '4');
INSERT INTO settings ("key", "title", "description", "type", "value", "editable", "position")
VALUES ('cacheMenuItemExpireAfterAccess', 'settings.cache.menu-item.expire-after-access.label',
        'settings.cache.menu-item.expire-after-access.helper', 'int', '0', '0', '5');
INSERT INTO settings ("key", "title", "description", "type", "value", "editable", "position")
VALUES ('cacheMenuItemExpireAfterWrite', 'settings.cache.menu-item.expire-after-write.label',
        'settings.cache.menu-item.expire-after-write.helper', 'int', '86400', '0', '6');
INSERT INTO settings ("key", "title", "description", "type", "value", "editable", "position")
VALUES ('cacheTestScenarioResultMaxSize', 'settings.cache.test-scenario-result.max-size.label',
        'settings.cache.test-scenario-result.max-size.helper', 'int', '100', '0', '7');
INSERT INTO settings ("key", "title", "description", "type", "value", "editable", "position")
VALUES ('cacheTestScenarioResultExpireAfterAccess', 'settings.cache.test-scenario-result.expire-after-access.label',
        'settings.cache.test-scenario-result.expire-after-access.helper', 'int', '300', '0', '8');
INSERT INTO settings ("key", "title", "description", "type", "value", "editable", "position")
VALUES ('cacheTestScenarioResultExpireAfterWrite', 'settings.cache.test-scenario-result.expire-after-write.label',
        'settings.cache.test-scenario-result.expire-after-write.helper', 'int', '0', '0', '9');
INSERT INTO settings ("key", "title", "description", "type", "value", "editable", "position")
VALUES ('cacheTranslationKeyMaxSize', 'settings.cache.translation-key.max-size.label',
        'settings.cache.translation-key.max-size.helper', 'int', '10000', '0', '10');
INSERT INTO settings ("key", "title", "description", "type", "value", "editable", "position")
VALUES ('cacheTranslationKeyExpireAfterAccess', 'settings.cache.translation-key.expire-after-access.label',
        'settings.cache.translation-key.expire-after-access.helper', 'int', '0', '0', '11');
INSERT INTO settings ("key", "title", "description", "type", "value", "editable", "position")
VALUES ('cacheTranslationKeyExpireAfterWrite', 'settings.cache.translation-key.expire-after-write.label',
        'settings.cache.translation-key.expire-after-write.helper', 'int', '3600', '0', '12');

CREATE UNIQUE INDEX settings_key_index ON settings ("key");

CREATE TABLE users
(
    id       INTEGER PRIMARY KEY AUTOINCREMENT,
    enabled  INTEGER (1) NOT NULL DEFAULT (1),
    username TEXT NOT NULL,
    password TEXT NOT NULL,
    email    TEXT NOT NULL,
    role     TEXT DEFAULT 'user'
);

INSERT INTO users ("username", "password", "email", "role")
VALUES ('test', '$2a$10$iZNmfanuP/Pn8OntMEsEKe6nH5JzL650v3zZM4aFNw4D36Wbq8ofG', 'test@myalerts.org', 'ROLE_USER');
INSERT INTO users ("username", "password", "email", "role")
VALUES ('admin', '$2a$10$18ldMrqn.vZIPvPUemB40eR5OTXIjLOAVrdFQdGCF6Bmh1l4ceYH2', 'admin@myalerts.org', 'ROLE_ADMIN');

CREATE UNIQUE INDEX users_username_index ON users (username);

CREATE TRIGGER onScenarioDelete
    AFTER DELETE
    ON scenarios
    FOR EACH ROW
BEGIN
    DELETE FROM results WHERE scenario_id = id;
END;