import { defineStore } from 'pinia'

type ThemeMode = 'light' | 'dark'
type Language = 'zh' | 'en'

const THEME_KEY = 'railway-theme'
const LANG_KEY = 'railway-lang'

export const useUiStore = defineStore('ui', {
  state: () => ({
    theme: (localStorage.getItem(THEME_KEY) as ThemeMode) || 'light',
    language: (localStorage.getItem(LANG_KEY) as Language) || 'zh'
  }),
  actions: {
    setTheme(mode: ThemeMode) {
      this.theme = mode
      localStorage.setItem(THEME_KEY, mode)
      document.documentElement.setAttribute('data-theme', mode)
    },
    toggleTheme() {
      this.setTheme(this.theme === 'light' ? 'dark' : 'light')
    },
    setLanguage(lang: Language) {
      this.language = lang
      localStorage.setItem(LANG_KEY, lang)
      document.documentElement.lang = lang === 'zh' ? 'zh-CN' : 'en'
    },
    toggleLanguage() {
      this.setLanguage(this.language === 'zh' ? 'en' : 'zh')
    },
    init() {
      this.setTheme(this.theme)
      this.setLanguage(this.language)
    }
  }
})
