import SwiftUI
import UIKit
import ComposeApp  // Ensure this module is imported


struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
 
struct ContentView: View {
    @StateObject private var locationManager = LocationManager()
    
    
    var body: some View {
        ComposeView()
            .ignoresSafeArea(.all)  // This will make the content ignore safe areas
            .onAppear {
                locationManager.requestPermission()
            }
            .alert("Location Permission Needed", isPresented: $locationManager.showCustomAlert) {
                Button("Settings") {
                    if let url = URL(string: UIApplication.openSettingsURLString) {
                        UIApplication.shared.open(url)
                    }
                }
                Button("Cancel", role: .cancel) {}
            } message: {
                Text("Enable location services in Settings to use this feature.")
            }
    }
    
}


