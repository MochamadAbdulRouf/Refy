---
name: Nocturne Audio
colors:
  surface: '#131315'
  surface-dim: '#131315'
  surface-bright: '#39393b'
  surface-container-lowest: '#0e0e10'
  surface-container-low: '#1c1b1d'
  surface-container: '#201f22'
  surface-container-high: '#2a2a2c'
  surface-container-highest: '#353437'
  on-surface: '#e5e1e4'
  on-surface-variant: '#cbc3d7'
  inverse-surface: '#e5e1e4'
  inverse-on-surface: '#313032'
  outline: '#958ea0'
  outline-variant: '#494454'
  surface-tint: '#d0bcff'
  primary: '#d0bcff'
  on-primary: '#3c0091'
  primary-container: '#a078ff'
  on-primary-container: '#340080'
  inverse-primary: '#6d3bd7'
  secondary: '#ddb7ff'
  on-secondary: '#490080'
  secondary-container: '#6f00be'
  on-secondary-container: '#d6a9ff'
  tertiary: '#c0c1ff'
  on-tertiary: '#1000a9'
  tertiary-container: '#8083ff'
  on-tertiary-container: '#0d0096'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#e9ddff'
  primary-fixed-dim: '#d0bcff'
  on-primary-fixed: '#23005c'
  on-primary-fixed-variant: '#5516be'
  secondary-fixed: '#f0dbff'
  secondary-fixed-dim: '#ddb7ff'
  on-secondary-fixed: '#2c0051'
  on-secondary-fixed-variant: '#6900b3'
  tertiary-fixed: '#e1e0ff'
  tertiary-fixed-dim: '#c0c1ff'
  on-tertiary-fixed: '#07006c'
  on-tertiary-fixed-variant: '#2f2ebe'
  background: '#131315'
  on-background: '#e5e1e4'
  surface-variant: '#353437'
typography:
  display-hero:
    fontFamily: Plus Jakarta Sans
    fontSize: 40px
    fontWeight: '800'
    lineHeight: 48px
    letterSpacing: -0.03em
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-lg-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 26px
    fontWeight: '700'
    lineHeight: 34px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 22px
    fontWeight: '600'
    lineHeight: 28px
    letterSpacing: -0.015em
  headline-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
    letterSpacing: -0.01em
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '500'
    lineHeight: 24px
    letterSpacing: -0.005em
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
    letterSpacing: 0em
  body-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
    letterSpacing: 0.01em
  label-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 18px
    letterSpacing: 0.01em
  label-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.02em
  label-xs:
    fontFamily: Plus Jakarta Sans
    fontSize: 10px
    fontWeight: '700'
    lineHeight: 12px
    letterSpacing: 0.06em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1rem
  gutter-mobile: 0.75rem
  margin: 1.5rem
  margin-mobile: 1rem
  space-xxs: 0.25rem
  space-xs: 0.5rem
  space-sm: 0.75rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
  space-2xl: 3rem
---

## Brand & Style

This design system channels an immersive, late-night sonic atmosphere engineered specifically for modern mobile audio streaming. Balancing high-energy digital culture with refined, ergonomic utility, it immerses listeners in an ambient visual landscape where media art takes center stage.

The style unites deep OLED black spatial depths with sleek glassmorphism and radiant violet gradients. High-contrast typography cuts cleanly through the dark field, while floating translucent modules, edge glows, and layered acrylic blurs create dimensional tactility. The emotional resonance is focused, electric, and premium—evoking the intimacy of closed-back studio monitors paired with the adrenaline of a live, neon-lit electronic performance.

## Colors

The palette establishes an absolute contrast environment anchored in deep true blacks and energized by luminescent spectrums:

- **Base Canvas (`#09090B`):** Pure OLED black that eliminates boundary friction on mobile screens and conserves battery life.
- **Surface Elevation (`#141418`):** Dark charcoal tier for cards, sheet panels, and baseline containers, sitting just above the absolute black.
- **Primary Accent (`#8B5CF6`):** Vibrant electric purple reserved for primary actions, active audio waveforms, and focal touch points.
- **Spectrum Gradients (`#A855F7` to `#6366F1`):** Dynamic neon violet and indigo blending applied to progress fills, hero artist backdrops, active playback aura, and premium badges.
- **Typography & Content Hierarchy:**
  - **Primary Text (`#FFFFFF`):** High-clarity white for track titles, prominent headers, and high-priority glyphs.
  - **Metadata & Subtitles (`#A1A1AA`):** Muted zinc for artist names, timestamps, durations, and secondary labels.
  - **Subtle Boundaries & Dividers (`#27272A`):** Low-contrast boundaries applied at 40–60% opacity for glass edge reflection.

## Typography

Typography relies entirely on **Plus Jakarta Sans**, chosen for its crisp geometric forms, open apertures, and modern warmth that remains legible against pitch-black backgrounds.

- **Weight Distinctions:** Headlines use bold and extra-bold weights (`700` and `800`) with tighter letter-spacing to command visual authority. Body copy relies on medium (`500`) and regular (`400`) to guarantee effortless scan-ability during rapid scrolling.
- **Optical Brightness Balancing:** Pure white (`#FFFFFF`) is reserved for primary titles, headlines, and active track states. All secondary body and label instances default to muted zinc (`#A1A1AA`), preventing eye strain across dense track listings and metadata rows.

## Layout & Spacing

The layout is built around a mobile-first fluid model governed by vertical scanning rhythms and thumb-driven ergonomics.

- **Grid & Safe Margins:** Uses a 4-column fluid layout on mobile screens expanding to 8 and 12 columns on tablet and desktop. Mobile horizontal edges enforce a strict `margin-mobile` of 16px (`1rem`), widening to 24px (`1.5rem`) on larger viewing viewports.
- **Audio Scroller Grids:** Horizontal carousels for albums, playlists, and artists feature `gutter-mobile` (12px) gaps, letting leading card edges bleed slightly past viewport boundaries to invite discovery.
- **Thumb Zone Clearance:** Persistent floating mini-players and bottom navigation bars are elevated above a mandatory 88px safe-area spacer, ensuring content is never obscured by global playback elements.

## Elevation & Depth

Depth is established without traditional opaque shadows. It uses a combination of deep tonal stacking, frosted glass surfaces, and localized radiant glows:

- **Level 0 (Canvas):** Pure `#09090B`. The deep bed for all scrolling content.
- **Level 1 (Structural Cards & Modules):** Solid `#141418` dark charcoal surfaces finished with a 1px hairline stroke (`rgba(255, 255, 255, 0.06)`).
- **Level 2 (Floating Glass Layers):** Sticky headers, mini-players, and bottom navigation draw on `rgba(20, 20, 24, 0.72)` with a 24px backdrop blur (`backdrop-filter: blur(24px)`) and a subtle top-lit rim stroke (`rgba(255, 255, 255, 0.12)`).
- **Level 3 (Modal Overlays & Full Player):** `rgba(9, 9, 11, 0.88)` with a 32px backdrop blur, shifting focus entirely to album artwork and lyrics.
- **Radiant Accents (Neon Aura):** Floating playback controls and active playlist artwork emit colored ambient shadows: `box-shadow: 0 12px 32px -4px rgba(139, 92, 246, 0.35)`.

## Shapes

The interface balances soft industrial minimalism with ergonomic curves:

- **Cards & Surfaces (`0.75rem` to `1rem`):** Media modules, playlist cards, and dialog sheets use medium rounded corners (`rounded-lg` at 16px) to echo contemporary device hardware.
- **Interactive Micro-Elements (`0.5rem`):** Checkboxes, dropdown selects, and badge tags utilize 8px radii.
- **Full Pill Geometry (`9999px`):** All primary touch controls—such as play/pause toggles, active filter chips, context action buttons, and scrub thumbs—are fully circular or pill-shaped, providing maximum tactile appeal and finger target affordance.

## Components

### Buttons & Interactive Controls
- **Primary Play Buttons:** Circular pill buttons wrapped in the primary gradient (`#A855F7` to `#6366F1`) with pure white icons. On hover or active state, they radiate an electric violet glow (`rgba(139, 92, 246, 0.45)`).
- **Secondary Actions:** Dark charcoal backgrounds (`#141418`) with white text and a 1px border of `rgba(255, 255, 255, 0.12)`.
- **Ghost & Media Controls:** Transparent backgrounds displaying `#A1A1AA` icons that brighten to `#FFFFFF` with a slight scale transform on touch.

### Chips & Tags
- **Filter & Genre Chips:** Pill-shaped (`rounded-full`) with a vertical padding of 6px and horizontal padding of 14px.
  - *Inactive:* Semi-transparent charcoal (`rgba(20, 20, 24, 0.8)`), text in `#A1A1AA`.
  - *Active:* Solid electric purple (`#8B5CF6`) or radiant gradient, text in `#FFFFFF`.

### Track Lists & Row Items
- **Structure:** 56px minimum row height containing: 44px rounded album thumbnail, stacked title (`#FFFFFF`) and metadata (`#A1A1AA`), and an optional right-aligned duration or overflow button.
- **Playing State:** Title shifts to `#8B5CF6` accompanied by an animated 3-bar equalizer glyph; row background transitions to a subtle `rgba(139, 92, 246, 0.08)`.

### Cards & Media Tiles
- **Album / Playlist Cards:** Square aspect-ratio cover art with an 8px radius nestled inside a `#141418` container with 12px inner padding. Includes a hovering play button that floats into view on interaction.
- **Surface Elevation Cards:** Subtle top-lit highlight (`border-t: 1px solid rgba(255, 255, 255, 0.1)`) that reinforces physical dimensionality.

### Input Fields & Search Bars
- **Search Surface:** Pill or rounded container filled with `#141418`, featuring an inset search glyph in `#A1A1AA`. Active states produce an outline glow in `#8B5CF6` with placeholder text in `#71717A`.

### Floating Mini-Player (Signature Component)
- **Visuals:** Suspended 8px above the bottom tab bar. Formed with a 16px radius, frosted glass backdrop (`rgba(20, 20, 24, 0.85)`, `blur(20px)`), glowing outer stroke, track progress bar integrated along the bottom edge in `#8B5CF6`, and quick play/pause/skip triggers.