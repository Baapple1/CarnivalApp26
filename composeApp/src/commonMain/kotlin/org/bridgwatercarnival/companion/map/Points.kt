package org.bridgwatercarnival.companion.map

import androidx.compose.ui.graphics.Color

object Points {
    sealed class MarkerCategory(val color: Color) {
        data object Toilet : MarkerCategory(Color.White)
        data object CarPark : MarkerCategory(Color.White)
        data object Start : MarkerCategory(Color.White)
        data object Finish : MarkerCategory(Color.White)
        data object Entertainment : MarkerCategory(Color.White)
        data object Break : MarkerCategory(Color.White)
        data object Hospitality : MarkerCategory(Color.White)
        data object Grandstand : MarkerCategory(Color.White)
        data object Assemble : MarkerCategory(Color.White)
        data object Infomation : MarkerCategory(Color.White)
    }

    val routeCoordinates = listOf(
        Point(51.12477215505126, -2.9830871186419268),//rest area
        Point(51.137008671408765, -2.9821982129444553),
        Point(51.13734989666378, -2.9822972655389157),
        Point(51.137567185119096, -2.9825506423347456),
        Point(51.13814070859791, -2.983510999535162),//start

        Point(51.1354508035565, -2.988033906916332),
        Point(51.13529485507741, -2.988224477974922),
        Point(51.134634557221474, -2.9889107524380907),
        Point(51.13428460805447, -2.9894173018236283),
        Point(51.13404060970228, -2.9901579544625445),
        Point(51.13304136490078, -2.99232436354113),
        Point(51.13179808824231, -2.994379674871416),
        Point(51.131600555229824, -2.9948981317186574),
        Point(51.1311706275172, -2.9951943927742235),
        Point(51.12997378059595, -2.996416469891126),
        Point(51.1277310616391, -2.9981570037509138),
        Point(51.1275001889476, -2.9984026855279255),

        Point(51.12703859846992, -2.999022048706047),
        Point(51.1265830770142, -2.999873673275323),
        Point(51.12585423336373, -3.001634987313104),
        Point(51.1258724545956, -3.0016156323821166),
        Point(51.12686853764336, -3.0023801588051096),
        Point(51.12697786252328, -3.0025253220499812),
        Point(51.12716614366811, -3.002844681272394),
        Point(51.12754877715027, -3.004093085178294),
        Point(51.127962069955494, -3.0043459429178982),
        Point(51.12802766328838, -3.0043368544466067),
        Point(51.12808184901487, -3.004245969733691),

        Point(51.12822941743123, -3.003876530278284),
        Point(51.12833005037789, -3.003819262040659),
        Point(51.12832476302272, -3.003806519462665),
        Point(51.12843685444757, -3.003836287768945),
        Point(51.12843685444757, -3.003836287768945),
        Point(51.12853583288644, -3.0041285914828952),
        Point(51.12841968584332, -3.0045148295523707),

        Point(51.127774980287846, -3.0063977403063684),
        Point(51.12767061457545, -3.007537679026928),
        Point(51.12767061457545, -3.007537679026928),
        Point(51.127862997984394, -3.007986705576586),
        Point(51.12817777641823, -3.0084426810869638),
        Point(51.12841512130009, -3.00845877433949),
        Point(51.12847908638276, -3.0083917191216756),
        Point(51.12886960810539, -3.0078579595608113),
        Point(51.12931066832476, -3.0072563413877935),  //first part of route
        Point(51.12940028009625, -3.0071043359489686),
        Point(51.12942629638444, -3.006910874481373),
        Point(51.12960551931007, -3.006123209936219),
        Point(51.12972981867315, -3.0056533749442274),

        Point(51.13002755764142, -3.0048012232427146),
        Point(51.1301316212928, -3.004437331434618)
    )

    // Adjusted Rest Area Coordinates
    val restAreaPoints = listOf(
        Point(51.12703859846992, -2.999022048706047), // Start of Red Line (Rest Area)
        Point(51.1265830770142, -2.999873673275323),
        Point(51.12585423336373, -3.001634987313104),
        Point(51.1258724545956, -3.0016156323821166),  // End of Red Line (Rest Area)
        // End of Red Line (Rest Area)
    )

    val lineUpCoordinates = listOf(
        Point(51.12477215505126, -2.9830871186419268),
        Point(51.137008671408765, -2.9821982129444553),
        Point(51.13734989666378, -2.9822972655389157),//Route
        Point(51.137567185119096, -2.9825506423347456),

        Point(51.13814070859791, -2.983510999535162)
    )

    val finishLine = listOf(
        Point(	51.12972981867315, -3.0056533749442274), //finish
        Point(51.13002755764142, -3.0048012232427146),
        Point(51.1301316212928, -3.004437331434618),
    )

    val townCenter = listOf(
        Point(51.12822941743123, -3.003876530278284),
        Point(51.12833005037789, -3.003819262040659),
        Point(51.12832476302272, -3.003806519462665),
        Point(51.12843685444757, -3.003836287768945),
        Point(51.12843685444757, -3.003836287768945),
        Point(51.12853583288644, -3.0041285914828952),
        Point(51.12841968584332, -3.0045148295523707)
    )

    val markers = listOf(
        //Infomation
        Point(51.127980, -3.006022) to Pair("Carnival Center", MarkerCategory.Infomation),
        Point(51.13859334226118, -2.9828148242683343) to Pair("Bath Road Toilets", MarkerCategory.Toilet),
        Point(51.13472919220898, -2.988790785956787) to Pair("College Way Toilets", MarkerCategory.Toilet),
        Point(51.13181970939384, -2.9942592646013617) to Pair("Bath Road Toilets", MarkerCategory.Toilet),
        Point(51.12825391993348, -3.00506363884519) to Pair("High Street Toilets", MarkerCategory.Toilet),
        Point(51.12747273797139, -3.0086068452609993) to Pair("Penel Orlieu Toilets", MarkerCategory.Toilet),
        Point(51.12898868296648, -3.0077379894433998) to Pair("Mount Street Toilets", MarkerCategory.Toilet),
        Point(51.1257712480152, -3.0014083630247077) to Pair("Taunton Road Toilets", MarkerCategory.Toilet),
        Point(51.12765021466968, -2.99827390387256) to Pair("Broadway Toilets", MarkerCategory.Toilet),
        Point(51.13814070859791, -2.983510999535162) to Pair("Start of procession (6PM)", MarkerCategory.Start),
        Point(51.13133663546705, -2.982485442673152) to Pair("Assemble Road (Line-Up)- closed from 9AM", MarkerCategory.Assemble),
        Point(51.1301316212928, -3.00443733143461) to Pair("End of procession", MarkerCategory.Finish),
        Point(51.12648324800076, -3.000310109009763) to Pair("Rest Area (No Viewing)", MarkerCategory.Break),
        Point(51.12484026457824, -3.0029453985243886) to Pair("Morrison's car park (500 spaces) - TA6 3LN", MarkerCategory.CarPark),
        Point(51.12511284766959, -3.005504556856558) to Pair("B&M car park (500 spaces) - TA6 3LN", MarkerCategory.CarPark),
        Point(51.12378474061676, -3.014021403522863) to Pair("ST Matthews field (1000 spaces) - TA6 7EU", MarkerCategory.CarPark),
        Point(51.13113154634135, -3.0033088940047166) to Pair("Northgate (161 spaces) - TA6 3EU", MarkerCategory.CarPark),
        Point(51.13057513122341, -2.999515744804497) to Pair("Asda car park (300 spaces) - TA6 4QJ", MarkerCategory.CarPark),
        Point(51.12673088937425, -3.002259690732969) to Pair("Cooperate Hospitality / Pre-bookings", MarkerCategory.Hospitality),
        Point(51.12832476302272, -3.003806519462665) to Pair("Town Center / Daytime entertainment", MarkerCategory.Entertainment),
        Point(51.136255904015414, -2.999384322992658) to Pair("Wickes car park- TA6 4DH", MarkerCategory.CarPark),
        Point(51.1409344134999, -2.9753077989478713) to Pair("Bridgwater Hospital", MarkerCategory.CarPark),
        Point(51.14358039547969, -2.9702075153416083) to Pair("Morganians Rugby Football Club- TA7 8QW", MarkerCategory.CarPark),

        Point(51.12897248560882, -2.997499652126621) to Pair("Grand Stand(Pre-book only)", MarkerCategory.Grandstand),
    )
}
