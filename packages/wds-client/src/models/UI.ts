export enum Theme {
  Reversed = 'reversed',
  Standard = 'standard'
}

export interface UI {
  theme: Theme
  showMobileNav: boolean
  showSidebar: boolean
  isSideClosed: boolean
  isTerminalExpanded: boolean
  toggleTerminalSize?: () => void
  setTheme?: (theme: Theme) => void
}
