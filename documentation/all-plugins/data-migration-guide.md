---
title: Data Migration Guide
sidebar_position: 10
---

This is a guide for changing data handler (e.g. yaml -> mysql), or for updating eco from a version before 6.74.0.

## For Networks

If you use the same database on multiple servers, follow these steps:

1) Shut down all servers.

2) Go to `/plugins/eco/config.yml` and make sure `perform-data-migration` is only set to `true` for **one** server.

3) On the server with data migration enabled, add `-Ddisable.watchdog=true` to startup flags.

4) Update `data-handler` to your new database in config on that server. If you are upgrading from before 6.74.0, **do not change this**. You can migrate again afterwards, but do not change data handler while updating eco.

5) Start that server, leave other servers off until migration is complete.

6) Wait for migration to complete. The server will restart automatically once migration is done.

7) Remove `-Ddisable.watchdog=true` from startup flags.

8) Turn your other servers back on.

## For Single Servers

If your server is not on a network, follow these steps:

1) Shut down your server.

2) Add `-Ddisable.watchdog=true` to startup flags.

3) Update `data-handler` to your new database in config. If you are upgrading from before 6.74.0, **do not change this**. You can migrate again afterwards, but do not change data handler while updating eco.

4) Start the server and wait for migration to complete. The server will restart automatically once migration is done.

5) Remove `-Ddisable.watchdog=true` from startup flags.
