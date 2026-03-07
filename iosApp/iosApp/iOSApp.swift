import SwiftUI
import ComposeApp
import MapKit
import FirebaseCore

@main
struct iOSApp: App {
    // Add the delegate adaptor for Firebase
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    init() {
        // Configure Firebase
        FirebaseApp.configure()
        
        CarnivalMapKt.getMapDelegate = {
            MapViewDelegate()
        }
        
        CarnivalMapKt.coloredPolyLine = { (points, count, color) in
            var mapped: [CLLocationCoordinate2D] = []
            for i in (0 ..< points.size) {
                let value = points.get(index: i)
                let lat = value!.lat
                let long = value!.long_
                mapped.append(CLLocationCoordinate2D(latitude: lat, longitude: long))
            }
    
            let line = ColoredPolyLine(coordinates: mapped, count: count.intValue)
            line.color = color
            
            // Set different line widths based on color for fancy effects
            if color == UIColor.systemPurple {
                line.lineWidth = 8.0 // Main route - extra wide
            } else if color == UIColor.magenta {
                line.lineWidth = 12.0 // Glow effect - very wide and soft
            } else if color == UIColor.yellow {
                line.lineWidth = 3.0 // Accent line - thin and bright
            } else if color == UIColor.red {
                line.lineWidth = 6.0 // Warning areas - prominent
            } else if color == UIColor.orange {
                line.lineWidth = 5.0 // Line-up area - distinct
            } else if color == UIColor.cyan {
                line.lineWidth = 2.0 // Sparkle effect - thin and sparkly
            } else {
                line.lineWidth = 4.0 // Default width
            }
            
            return line
        }
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

// Add the AppDelegate class in the same file
class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication,
                    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        return true
    }
}
