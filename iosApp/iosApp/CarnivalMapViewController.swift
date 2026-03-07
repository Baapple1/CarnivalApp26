import UIKit
import MapKit

class CarnivalMapViewController: UIViewController {
    private let mapView = MKMapView()
    private let mapDelegate = MapViewDelegate()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupMapView()
        addMarkers()
        centerMap()
    }
    
    private func setupMapView() {
        view.addSubview(mapView)
        mapView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            mapView.topAnchor.constraint(equalTo: view.topAnchor),
            mapView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            mapView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            mapView.bottomAnchor.constraint(equalTo: view.bottomAnchor)
        ])
        
        mapView.delegate = mapDelegate
        mapView.showsUserLocation = true
    }
    
    private func addMarkers() {
        // Add all markers from our data
        for (point, info) in MapPoints.markers {
            let annotation = CustomAnnotation(
                coordinate: point.coordinate,
                title: info.0,
                type: info.1
            )
            print("Adding marker: \(info.0) with type: \(info.1)")
            mapView.addAnnotation(annotation)
        }
        
        // Add route polyline
        let routeCoordinates = MapPoints.routeCoordinates.map { $0.coordinate }
        let polyline = ColoredPolyLine(coordinates: routeCoordinates, count: routeCoordinates.count)
        polyline.color = .systemBlue
        mapView.addOverlay(polyline)
    }
    
    private func centerMap() {
        // Center on Bridgwater
        let center = CLLocationCoordinate2D(latitude: 51.128, longitude: -2.993)
        let span = MKCoordinateSpan(latitudeDelta: 0.03, longitudeDelta: 0.03)
        let region = MKCoordinateRegion(center: center, span: span)
        mapView.setRegion(region, animated: true)
    }
} 