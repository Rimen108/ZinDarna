
function createIfMissing(databaseName, collectionNames) {
    const database = db.getSiblingDB(databaseName);

    collectionNames.forEach(name => {
        if (!database.getCollectionNames().includes(name)) {
            database.createCollection(name);
            print("Created: " + databaseName + "." + name);
        } else {
            print("Already exists: " + databaseName + "." + name);
        }
    });
}

createIfMissing("auth_db", ["users", "auditLogs"]);

createIfMissing("catalog_db", [
    "products",
    "categories",
    "promotions"
]);

createIfMissing("interaction_db", [
    "likes",
    "follows",
    "conversations"
]);

createIfMissing("project_db", ["projects"]);

createIfMissing("order_db", ["carts", "orders"]);

createIfMissing("notification_db", ["notifications"]);

createIfMissing("ai_db", [
    "aiConversations",
    "generatedImages"
]);

createIfMissing("review_db", ["reviews"]);

print("Zin Darna initialization completed!");
