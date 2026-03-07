import Foundation

class LocalizationManager {
    static let shared = LocalizationManager()
    
    private var bundle: Bundle?
    
    private init() {
        updateBundle()
        NotificationCenter.default.addObserver(self, selector: #selector(languageChanged), name: NSNotification.Name("LanguageChanged"), object: nil)
    }
    
    @objc private func languageChanged() {
        updateBundle()
    }
    
    private func updateBundle() {
        let languageCode = UserDefaults.standard.string(forKey: "AppLanguage") ?? "en"
        if let path = Bundle.main.path(forResource: languageCode, ofType: "lproj"),
           let bundle = Bundle(path: path) {
            self.bundle = bundle
        } else {
            self.bundle = Bundle.main
        }
    }
    
    func localizedString(for key: String) -> String {
        return bundle?.localizedString(forKey: key, value: nil, table: nil) ?? key
    }
}

// Extension to make string localization easier to use
extension String {
    var localized: String {
        return LocalizationManager.shared.localizedString(for: self)
    }
} 