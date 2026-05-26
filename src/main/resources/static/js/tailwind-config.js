// File: src/main/resources/static/js/tailwind-config.js
tailwind.config = {
    darkMode: "class",
    theme: {
        extend: {
            "colors": {
                "surface-container": "#eeeeec",
                "surface-bright": "#f9f9f7",
                "surface-container-low": "#f4f4f2",
                "inverse-primary": "#eabda0",
                "surface-variant": "#e2e3e1",
                "secondary-fixed": "#f3e2ac",
                "on-primary": "#ffffff",
                "surface-tint": "#79573f",
                "on-secondary-container": "#706439",
                "surface-container-lowest": "#ffffff",
                "on-tertiary": "#ffffff",
                "on-secondary-fixed-variant": "#51461e",
                "secondary-fixed-dim": "#d6c692",
                "on-primary-fixed-variant": "#5f402a",
                "on-error": "#ffffff",
                "primary-container": "#6f4e37",
                "tertiary-fixed-dim": "#c6c6c6",
                "on-error-container": "#93000a",
                "primary-fixed": "#ffdcc6",
                "on-tertiary-fixed-variant": "#454747",
                "surface-container-high": "#e8e8e6",
                "background": "#f9f9f7",
                "on-secondary": "#ffffff",
                "outline": "#82746d",
                "on-primary-fixed": "#2d1604",
                "on-secondary-fixed": "#231b00",
                "secondary": "#6a5e33",
                "primary-fixed-dim": "#eabda0",
                "tertiary-container": "#545555",
                "inverse-surface": "#2f3130",
                "on-tertiary-fixed": "#1a1c1c",
                "on-tertiary-container": "#cacaca",
                "inverse-on-surface": "#f1f1ef",
                "error": "#ba1a1a",
                "surface-dim": "#dadad8",
                "tertiary-fixed": "#e2e2e2",
                "surface": "#f9f9f7",
                "on-primary-container": "#eec1a4",
                "primary": "#553722",
                "secondary-container": "#f3e2ac",
                "surface-container-highest": "#e2e3e1",
                "on-surface-variant": "#50453e",
                "on-background": "#1a1c1b",
                "outline-variant": "#d4c3ba",
                "on-surface": "#1a1c1b",
                "tertiary": "#3c3e3e",
                "error-container": "#ffdad6"
            },
            "borderRadius": {
                "DEFAULT": "0.25rem",
                "lg": "0.5rem",
                "xl": "0.75rem",
                "full": "9999px"
            },
            "spacing": {
                "xl": "64px",
                "margin": "32px",
                "gutter": "24px",
                "sm": "12px",
                "base": "8px",
                "xs": "4px",
                "md": "24px",
                "lg": "40px"
            },
            "fontFamily": {
                "body-lg": ["Manrope"],
                "headline-lg": ["Manrope"],
                "headline-md": ["Manrope"],
                "body-md": ["Manrope"],
                "headline-lg-mobile": ["Manrope"],
                "label-md": ["Hanken Grotesk"],
                "headline-sm": ["Manrope"]
            },
            "fontSize": {
                "body-lg": ["16px", { "lineHeight": "24px", "fontWeight": "400" }],
                "headline-lg": ["32px", { "lineHeight": "40px", "fontWeight": "700" }],
                "headline-md": ["24px", { "lineHeight": "32px", "fontWeight": "600" }],
                "body-md": ["14px", { "lineHeight": "20px", "fontWeight": "400" }],
                "headline-lg-mobile": ["26px", { "lineHeight": "32px", "fontWeight": "700" }],
                "label-md": ["12px", { "lineHeight": "16px", "letterSpacing": "0.05em", "fontWeight": "600" }],
                "headline-sm": ["20px", { "lineHeight": "28px", "fontWeight": "600" }]
            }
        }
    }
}