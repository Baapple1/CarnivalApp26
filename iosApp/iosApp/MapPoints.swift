import Foundation
import MapKit

struct Point {
    let latitude: Double
    let longitude: Double
    
    var coordinate: CLLocationCoordinate2D {
        CLLocationCoordinate2D(latitude: latitude, longitude: longitude)
    }
}

struct MapPoints {
    static let markers: [(Point, (String, String))] = [
        (Point(latitude: 51.13859334226118, longitude: -2.9828148242683343), ("Bath Road Toilets", "toilet_marker")),
        (Point(latitude: 51.13472919220898, longitude: -2.988790785956787), ("College Way Toilets", "toilet_marker")),
        (Point(latitude: 51.13181970939384, longitude: -2.9942592646013617), ("Bath Road Toilets", "toilet_marker")),
        (Point(latitude: 51.12825391993348, longitude: -3.00506363884519), ("High Street Toilets", "toilet_marker")),
        (Point(latitude: 51.12747273797139, longitude: -3.0086068452609993), ("Penel Orlieu Toilets", "toilet_marker")),
        (Point(latitude: 51.12898868296648, longitude: -3.0077379894433998), ("Mount Street Toilets", "toilet_marker")),
        (Point(latitude: 51.1257712480152, longitude: -3.0014083630247077), ("Taunton Road Toilets", "toilet_marker")),
        (Point(latitude: 51.12765021466968, longitude: -2.99827390387256), ("Broadway Toilets", "toilet_marker")),
        (Point(latitude: 51.13814070859791, longitude: -2.983510999535162), ("Start of procession (6PM)", "Start")),
        (Point(latitude: 51.13133663546705, longitude: -2.982485442673152), ("Assemble Road (Line-Up)- closed from 9AM", "Assemble")),
        (Point(latitude: 51.1301316212928, longitude: -3.00443733143461), ("End of procession", "Finish")),
        (Point(latitude: 51.12648324800076, longitude: -3.000310109009763), ("Rest Area (No Viewing)", "Break")),
        (Point(latitude: 51.12484026457824, longitude: -3.0029453985243886), ("Morrison's car park (500 spaces) - TA6 3LN", "CarPark")),
        (Point(latitude: 51.12511284766959, longitude: -3.005504556856558), ("B&M car park (500 spaces) - TA6 3LN", "CarPark")),
        (Point(latitude: 51.12378474061676, longitude: -3.014021403522863), ("ST Matthews field (1000 spaces) - TA6 7EU", "CarPark")),
        (Point(latitude: 51.13113154634135, longitude: -3.0033088940047166), ("Northgate (161 spaces) - TA6 3EU", "CarPark")),
        (Point(latitude: 51.13057513122341, longitude: -2.999515744804497), ("Asda car park (300 spaces) - TA6 4QJ", "CarPark")),
        (Point(latitude: 51.12673088937425, longitude: -3.002259690732969), ("Cooperate Hospitality / Pre-bookings", "Hospitality")),
        (Point(latitude: 51.12832476302272, longitude: -3.003806519462665), ("Town Center / Daytime entertainment", "Entertainment")),
        (Point(latitude: 51.136255904015414, longitude: -2.999384322992658), ("Wickes car park- TA6 4DH", "CarPark")),
        (Point(latitude: 51.1409344134999, longitude: -2.9753077989478713), ("Bridgwater Hospital", "CarPark")),
        (Point(latitude: 51.14358039547969, longitude: -2.9702075153416083), ("Morganians Rugby Football Club- TA7 8QW", "CarPark")),
        (Point(latitude: 51.12897248560882, longitude: -2.997499652126621), ("Grand Stand(Pre-book only)", "Grandstand"))
    ]
    
    static let routeCoordinates: [Point] = [
        Point(latitude: 51.12477215505126, longitude: -2.9830871186419268),
        Point(latitude: 51.137008671408765, longitude: -2.9821982129444553),
        Point(latitude: 51.13734989666378, longitude: -2.9822972655389157),
        Point(latitude: 51.137567185119096, longitude: -2.9825506423347456),
        Point(latitude: 51.13814070859791, longitude: -2.983510999535162),
        // ... Add all other route coordinates here
    ]
} 