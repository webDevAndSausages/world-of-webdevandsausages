export enum Theme {
  Reversed = 'reversed',
  Standard = 'standard'
}

export interface UI {
  theme: Theme
  showMobileNav: boolean
  showSidebar: boolean
  isSideClosed: boolean
}
