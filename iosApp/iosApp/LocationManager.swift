import CoreLocation
import SwiftUI

class LocationManager: NSObject, ObservableObject, CLLocationManagerDelegate {
    private var locationManager = CLLocationManager()
    @Published var showCustomAlert = false
    private var hasRequestedPermission = false // ✅ Prevents duplicate requests

    override init() {
        super.init()
        locationManager.delegate = self
    }

    func requestPermission() {
        print("Requesting location permission...") // Debugging

        // ✅ Check if request was already made to prevent duplicate alerts
        if hasRequestedPermission { return }
        hasRequestedPermission = true

        if CLLocationManager.locationServicesEnabled() {
            switch locationManager.authorizationStatus {
            case .notDetermined:
                locationManager.requestWhenInUseAuthorization()
            case .denied, .restricted:
                showCustomAlert = true
            default:
                break
            }
        } else {
            showCustomAlert = true
        }
    }

    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        DispatchQueue.main.async {
            print("Authorization changed: \(status.rawValue)") // Debugging
            self.hasRequestedPermission = false // Allow re-request only if needed

            if status == .denied {
                self.showCustomAlert = true
            } else {
                self.showCustomAlert = false
            }
        }
    }
}
