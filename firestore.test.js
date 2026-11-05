const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Public: anyone can read products", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertSucceeds(unauthDb.collection("products").get());
});

test("Unauthenticated user: cannot create order", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(
    unauthDb.collection("orders").doc("order_1").set({
      userId: ALICE_UID,
      customerName: "Alice",
      customerPhone: "01234567890",
      governorate: "الإسكندرية",
      address: "شارع جمال عبد الناصر",
      items: [{ productId: "p1", title: "ملاية قطن", price: 500, quantity: 1 }],
      subtotal: 500,
      shippingFee: 50,
      total: 550,
      paymentMethod: "cash_on_delivery",
      paymentStatus: "pending",
      status: "جديد",
      createdAt: new Date(),
      updatedAt: new Date(),
    })
  );
});

test("Authenticated user: can create valid order for themselves", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("orders").doc("order_1").set({
      userId: ALICE_UID,
      customerName: "Alice",
      customerPhone: "01234567890",
      governorate: "الإسكندرية",
      address: "شارع جمال عبد الناصر",
      items: [{ productId: "p1", title: "ملاية قطن", price: 500, quantity: 1 }],
      subtotal: 500,
      shippingFee: 50,
      total: 550,
      paymentMethod: "cash_on_delivery",
      paymentStatus: "pending",
      status: "جديد",
      createdAt: new Date(),
      updatedAt: new Date(),
    })
  );
});

test("Authenticated user: cannot read another user's order", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("orders").doc("bob_order").set({
      userId: BOB_UID,
      customerName: "Bob",
      customerPhone: "01234567891",
      governorate: "الإسكندرية",
      address: "محرم بك",
      items: [{ productId: "p2", title: "لحاف فايبر", price: 1200, quantity: 1 }],
      subtotal: 1200,
      shippingFee: 50,
      total: 1250,
      paymentMethod: "cash_on_delivery",
      paymentStatus: "pending",
      status: "جديد",
      createdAt: new Date(),
      updatedAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("orders").doc("bob_order").get());
});

test("Authenticated user: can query own orders with userId filter", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("orders").doc("alice_order").set({
      userId: ALICE_UID,
      customerName: "Alice",
      customerPhone: "01234567890",
      governorate: "الإسكندرية",
      address: "العصافرة",
      items: [{ productId: "p1", title: "ملاية قطن", price: 500, quantity: 1 }],
      subtotal: 500,
      shippingFee: 50,
      total: 550,
      paymentMethod: "cash_on_delivery",
      paymentStatus: "pending",
      status: "جديد",
      createdAt: new Date(),
      updatedAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("orders").where("userId", "==", ALICE_UID).get()
  );
});
