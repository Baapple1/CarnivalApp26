import SwiftUI

enum Language: String, CaseIterable {
    case english = "en"
    case french = "fr"
    case spanish = "es"
    
    var displayName: String {
        switch self {
        case .english: return "English"
        case .french: return "Français"
        case .spanish: return "Español"
        }
    }
    
    var flagImage: String {
        switch self {
        case .english: return "flag.england"
        case .french: return "flag.france"
        case .spanish: return "flag.spain"
        }
    }
}

class LanguageManager: ObservableObject {
    @Published var currentLanguage: Language {
        didSet {
            UserDefaults.standard.set(currentLanguage.rawValue, forKey: "AppLanguage")
            UserDefaults.standard.synchronize()
            NotificationCenter.default.post(name: NSNotification.Name("LanguageChanged"), object: nil)
        }
    }
    
    init() {
        let savedLanguage = UserDefaults.standard.string(forKey: "AppLanguage") ?? "en"
        self.currentLanguage = Language(rawValue: savedLanguage) ?? .english
    }
}

struct LanguageSettingsView: View {
    @ObservedObject var languageManager: LanguageManager
    @Environment(\.dismiss) var dismiss
    
    var body: some View {
        NavigationView {
            List(Language.allCases, id: \.self) { language in
                HStack {
                    Image(language.flagImage)
                        .resizable()
                        .scaledToFit()
                        .frame(width: 30, height: 20)
                    
                    Text(language.displayName)
                        .padding(.leading, 8)
                    
                    Spacer()
                    
                    if language == languageManager.currentLanguage {
                        Image(systemName: "checkmark")
                            .foregroundColor(.blue)
                    }
                }
                .contentShape(Rectangle())
                .onTapGesture {
                    languageManager.currentLanguage = language
                    dismiss()
                }
            }
            .navigationTitle("Language")
            .navigationBarItems(trailing: Button("Done") {
                dismiss()
            })
        }
    }
}

// Preview provider for SwiftUI canvas
struct LanguageSettingsView_Previews: PreviewProvider {
    static var previews: some View {
        LanguageSettingsView(languageManager: LanguageManager())
    }
} 