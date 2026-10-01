---
title: Data Storage
sidebar_position: 10
---
## How eco stores data

Every eco plugin saves player and server data (levels, balances, progress, settings) through eco. You choose where that data lives once, in `/plugins/eco/config.yml`, and every plugin uses it.

```yaml
data-handler: sqlite
```

## Supported storage types

| `data-handler` | Type | Best for | Needs a separate server |
| --- | --- | --- | --- |
| `sqlite` | Local file (`data.db`) | Single servers. The default. | No |
| `mysql` | SQL database | Networks (BungeeCord/Velocity) | Yes |
| `mariadb` | SQL database | Networks (BungeeCord/Velocity) | Yes |
| `postgresql` (or `postgres`) | SQL database | Networks (BungeeCord/Velocity) | Yes |
| `mongodb` (or `mongo`) | Document database | Networks (BungeeCord/Velocity) | Yes |
| `redis` | In-memory database, standalone or Sentinel | Networks (BungeeCord/Velocity) | Yes |

If you run one server, keep `sqlite`: there is nothing to set up. If you run a network and want players to keep their data across servers, point every server at the same MySQL, MariaDB, PostgreSQL, MongoDB or Redis database.

`yaml` is no longer a storage type. A config that still says `yaml` is treated as `sqlite`, and any data left in `data.yml` is moved across automatically.

## SQLite

```yaml
data-handler: sqlite

sqlite:
  # The name of the database file, stored in the eco plugin folder.
  file: data.db

  # The table prefix to use for all tables.
  prefix: "eco_"
```

## MySQL / MariaDB

Both use the `mysql` section. Use `mariadb` if your database is MariaDB.

```yaml
data-handler: mysql

mysql:
  # The table prefix to use for all tables.
  prefix: "eco_"

  # The maximum number of connections.
  connections: 10

  host: localhost
  port: 3306
  database: database
  user: username
  password: p4ssw0rd
```

## PostgreSQL

```yaml
data-handler: postgresql

postgresql:
  # The table prefix to use for all tables.
  prefix: "eco_"

  # The maximum number of connections.
  connections: 10

  host: localhost
  port: 5432
  database: database
  user: username
  password: p4ssw0rd
```

## MongoDB

```yaml
data-handler: mongodb

mongodb:
  # The full MongoDB connection URL.
  url: "mongodb://user:password@localhost:27017"

  # The name of the database to use.
  database: eco

  # The collection to use for player data.
  collection: profiles
```

## Redis

Redis can be a single server (standalone) or a Sentinel setup with automatic failover. Standalone is the default; Sentinel is used only when `sentinel.master` is set.

```yaml
data-handler: redis

redis:
  # The prefix to use for all keys.
  prefix: "eco:"

  # The maximum number of Redis connections.
  connections: 10

  # Connection details for Redis.
  host: localhost
  port: 6379
  database: 0
  user: "" # Leave blank for password-only auth.
  password: ""
  ssl: false

  # Optional: connect through Redis Sentinel for automatic failover.
  # Leave master blank to connect to host/port directly.
  sentinel:
    master: ""
    nodes: [] # e.g. ["10.0.0.1:26379", "10.0.0.2:26379"]
    password: "" # Sentinel auth, if different from the data nodes.

  # If cached player data should be refreshed across servers sharing this Redis when it changes.
  # Every server must use data-handler: redis.
  sync: false
```

:::warning
Redis keeps data in memory. Because eco uses it as the only copy of your players' data, turn on persistence on your Redis server (AOF, `appendonly yes`, is recommended). Without persistence, restarting Redis deletes everything.
:::

### Sentinel

To use Sentinel, set `sentinel.master` to the name of your master set (the name in your `sentinel.conf`) and list at least one sentinel in `nodes`. eco asks the sentinels which server is the current master and follows it automatically after a failover. When Sentinel is on, `host` and `port` are ignored. `user`, `password`, `database` and `ssl` still apply to the data servers.

```yaml
redis:
  sentinel:
    master: mymaster
    nodes: ["10.0.0.1:26379", "10.0.0.2:26379", "10.0.0.3:26379"]
```

Redis Cluster is not supported.

### Cross-server sync

Each server keeps recently used data in memory. On a network, this means one server can show an old value for a player who was just changed on another server, for example a player looked up while offline, or data shared by the whole network.

With `sync: true`, a server tells the others through Redis whenever it saves a value, and they drop their cached copy and read the new one. Leaderboards on every server update too. It is off by default; turn it on for every server on the network at once.

Sync keeps copies fresh, but it does not merge changes. If two servers change the same value at the same moment, the last one saved wins.

## Changing storage type

When you change `data-handler`, eco moves your existing data to the new storage type on the next start. You do not need to export or import anything.

```yaml
# If data should be migrated automatically when changing data handler.
perform-data-migration: true

# How many milliseconds to wait between profiles while migrating.
migration-throttle-ms: 5
```

Take a backup before switching. Moving between any two storage types works, including to and from Redis.

## Keeping a plugin's data on one server

On a network you may want one plugin's data to stay on each server rather than be shared (for example, a per-server economy). Set this in that plugin's `config.yml`:

```yaml
use-local-storage: true
```

That plugin's data is then stored in the local SQLite file, whatever `data-handler` is set to in eco.
