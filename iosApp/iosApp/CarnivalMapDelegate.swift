import MapKit
import FirebaseCore

class CarnivalMapDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil) -> Bool {
        FirebaseApp.configure()
        return true
    }
}

class ColoredPolyLine: MKPolyline {
    var color: UIColor?
    var lineWidth: CGFloat = 4.0
}

enum MarkerType: String {
    case Toilet = "Toilet"
    case CarPark = "CarPark"
    case Start = "Start"
    case Finish = "Finish"
    case Entertainment = "Entertainment"
    case Break = "Break"
    case Hospitality = "Hospitality"
    case Grandstand = "Grandstand"
    case Assemble = "Assemble"
    case Infomation = "Infomation"
}

class CustomAnnotation: MKPointAnnotation {
    var type: MarkerType = .Toilet
}

class MapViewDelegate: NSObject, MKMapViewDelegate {
    func mapView(_ mapView: MKMapView, rendererFor overlay: MKOverlay) -> MKOverlayRenderer {
        if let polyline = overlay as? ColoredPolyLine {
            let renderer = MKPolylineRenderer(overlay: overlay)
            renderer.strokeColor = polyline.color ?? UIColor.red
            renderer.lineWidth = polyline.lineWidth
            
            // Add some fancy effects based on color
            if polyline.color == UIColor.systemPurple {
                // Main route - make it extra fancy
                renderer.alpha = 0.9
            } else if polyline.color == UIColor.magenta {
                // Glow effect - make it soft
                renderer.alpha = 0.4
            } else if polyline.color == UIColor.yellow {
                // Accent line - make it bright
                renderer.alpha = 0.8
            } else if polyline.color == UIColor.red {
                // Warning areas - make them prominent
                renderer.alpha = 0.8
            } else if polyline.color == UIColor.orange {
                // Line-up area - make it distinct
                renderer.alpha = 0.85
            } else if polyline.color == UIColor.cyan {
                // Sparkle effect - make it festive
                renderer.alpha = 0.6
            }
            
            return renderer
        }
        
        return MKOverlayRenderer()
    }
    
    func mapView(_ mapView: MKMapView, viewFor annotation: MKAnnotation) -> MKAnnotationView? {
        // Skip user location annotation
        if annotation is MKUserLocation {
            return nil
        }

        // Cast to our custom annotation type
        guard let customAnnotation = annotation as? CustomAnnotation else {
            print("Failed to cast annotation to CustomAnnotation")
            return nil
        }

        let identifier = "CustomAnnotation"
        var view = mapView.dequeueReusableAnnotationView(withIdentifier: identifier)

        if view == nil {
            view = MKAnnotationView(annotation: annotation, reuseIdentifier: identifier)
            view?.canShowCallout = true
        } else {
            view?.annotation = annotation
        }

        // Create a container view for the marker
        let containerView = UIView(frame: CGRect(x: 0, y: 0, width: 40, height: 40))
        containerView.backgroundColor = .white
        containerView.layer.cornerRadius = 20
        containerView.layer.borderWidth = 2

        // Create the image view for the icon
        let imageView = UIImageView(frame: CGRect(x: 5, y: 5, width: 30, height: 30))
        imageView.contentMode = .scaleAspectFit

        // Set the icon and color based on type
        switch customAnnotation.type {
        case .Toilet:
            imageView.image = UIImage(systemName: "toilet.fill")
            containerView.layer.borderColor = UIColor.systemBlue.cgColor
            imageView.tintColor = .systemBlue
        case .Start:
            imageView.image = UIImage(systemName: "flag.circle.fill")
            containerView.layer.borderColor = UIColor.systemGreen.cgColor
            imageView.tintColor = .systemGreen
        case .Finish:
            imageView.image = UIImage(systemName: "flag.circle.fill")
            containerView.layer.borderColor = UIColor.systemRed.cgColor
            imageView.tintColor = .systemRed
        case .CarPark:
            imageView.image = UIImage(systemName: "car.circle.fill")
            containerView.layer.borderColor = UIColor.systemBlue.cgColor
            imageView.tintColor = .systemBlue
        case .Break:
            imageView.image = UIImage(systemName: "cup.and.saucer.circle.fill")
            containerView.layer.borderColor = UIColor.systemOrange.cgColor
            imageView.tintColor = .systemOrange
        case .Entertainment:
            imageView.image = UIImage(systemName: "music.note.circle.fill")
            containerView.layer.borderColor = UIColor.systemPurple.cgColor
            imageView.tintColor = .systemPurple
        case .Hospitality:
            imageView.image = UIImage(systemName: "fork.knife.circle.fill")
            containerView.layer.borderColor = UIColor.systemBrown.cgColor
            imageView.tintColor = .systemBrown
        case .Grandstand:
            imageView.image = UIImage(systemName: "person.3.circle.fill")
            containerView.layer.borderColor = UIColor.systemIndigo.cgColor
            imageView.tintColor = .systemIndigo
        case .Assemble:
            imageView.image = UIImage(systemName: "person.2.circle.fill")
            containerView.layer.borderColor = UIColor.systemTeal.cgColor
            imageView.tintColor = .systemTeal
        case .Infomation:
            imageView.image = UIImage(systemName: "info.circle.fill")
            containerView.layer.borderColor = UIColor.systemGray.cgColor
            imageView.tintColor = .systemGray
        }

        // Add the image view to the container
        containerView.addSubview(imageView)

        // Convert the container view to an image
        let renderer = UIGraphicsImageRenderer(size: containerView.bounds.size)
        let image = renderer.image { context in
            containerView.layer.render(in: context.cgContext)
        }

        // Set the annotation view's image
        view?.image = image
        view?.centerOffset = CGPoint(x: -20, y: -20) // Center the marker on the coordinate

        return view
    }
}
