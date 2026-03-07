import UIKit
import SwiftUI

class ComposeViewControllerWrapper: UIViewController {
    override func viewDidLoad() {
        super.viewDidLoad()
        let composeView = ComposeView()
        composeView.ignoresSafeArea(.all)
        
        // Add compose view as child
        addChild(MainViewControllerKt.createMainViewController())
        view.addSubview(composeView)
        
        // Setup constraints
        composeView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            composeView.topAnchor.constraint(equalTo: view.topAnchor),
            composeView.bottomAnchor.constraint(equalTo: view.bottomAnchor),
            composeView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            composeView.trailingAnchor.constraint(equalTo: view.trailingAnchor)
        ])
    }
} 