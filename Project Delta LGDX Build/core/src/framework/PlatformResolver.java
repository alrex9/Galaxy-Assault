//package framework;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.pay.PurchaseManager;
//import com.badlogic.gdx.pay.PurchaseManagerConfig;
//import com.badlogic.gdx.pay.PurchaseObserver;
//import com.badlogic.gdx.pay.PurchaseSystem;
//
//public abstract class PlatformResolver {
//
//protected PurchaseManager mgr;
//
//public PlatformResolver () {    }
//
//public void initializeIAP (PurchaseManager mgr, PurchaseObserver purchaseObserver, PurchaseManagerConfig config) {
//
//    this.mgr = mgr;
//
//    // set and install the manager manually
//    if (mgr != null) {
//        PurchaseSystem.setManager(mgr);
//        PurchaseSystem.install(purchaseObserver, config);
//    } else {
//        Gdx.app.log("", "gdx-pay: initializeIAP(): purchaseManager == null => call PurchaseSystem.hasManager()");
//        if (PurchaseSystem.hasManager()) { // install and get the manager automatically via reflection
//            this.mgr = PurchaseSystem.getManager();
//            PurchaseSystem.install(purchaseObserver, config); // install the observer
//            Gdx.app.log("", "gdx-pay: installed manager: " + this.mgr.toString());
//        }
//    }
//}
//
//public void requestPurchase (String productString) {
//    // if (PurchaseSystem.hasManager()) {
//    if (mgr != null) {
//        PurchaseSystem.purchase(productString);
//        Gdx.app.log("gdx-pay", "PurchaseSystem.purchase");
//    } else {
//        Gdx.app.log("ERROR", "gdx-pay: requestPurchase(): purchaseManager == null");
//    }
//}
//
//public void requestPurchaseRestore () {
//    // if (PurchaseSystem.hasManager()) {
//    if (mgr != null) {
//        PurchaseSystem.purchaseRestore();
//    } else {
//        Gdx.app.log("ERROR", "gdx-pay: requestPurchaseRestore(): purchaseManager == null");
//    }
//}
//
//public PurchaseManager getPurchaseManager () {
//    return mgr;
//}
//}