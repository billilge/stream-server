rootProject.name = "stream-server"

include(
    "bootstrap",
    "api:common-api",
    "api:admin-api",
    "api:app-api",
    "core:common",
    "gateway:auth",
    "gateway:logging",
    "infrastructure:db",
    "infrastructure:client",
    "infrastructure:outbox",
)
