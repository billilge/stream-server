rootProject.name = "stream-server"

include(
    "bootstrap",
    "api:common-api",
    "api:admin-api",
    "api:app-api",
    "core:common",
    "core:domain:event",
    "core:domain:internal",
    "core:domain:welfare",
    "core:domain:auth",
    "gateway:auth",
    "gateway:logging",
    "infrastructure:db",
    "infrastructure:client",
    "infrastructure:outbox",
)
