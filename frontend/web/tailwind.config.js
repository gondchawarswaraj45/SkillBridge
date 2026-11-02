/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        brand: {
          50: '#EEF2FF',
          100: '#E0E7FF',
          200: '#C7D2FE',
          300: '#A5B4FC',
          400: '#818CF8',
          500: '#5146E5', // Primary Deep Indigo
          600: '#4338CA',
          700: '#3730A3',
          800: '#312E81',
          900: '#1E1B4B',
        },
        accent: {
          50: '#F5F3FF',
          100: '#EDE9FE',
          200: '#DDD6FE',
          300: '#C4B5FD',
          400: '#A78BFA',
          500: '#8067F5', // Secondary Purple
          600: '#7C3AED',
          700: '#6D28D9',
          800: '#5B21B6',
          900: '#4C1D95',
        },
        surface: {
          bg: '#F8F9FC',
          card: '#FFFFFF',
          border: '#E2E8F0',
          text: '#172033',
          muted: '#64748B',
        }
      },
      fontFamily: {
        sans: ['Plus Jakarta Sans', 'Inter', 'system-ui', 'sans-serif'],
      },
      boxShadow: {
        'soft-sm': '0 1px 3px 0 rgba(23, 32, 51, 0.05)',
        'soft-md': '0 4px 6px -1px rgba(23, 32, 51, 0.06), 0 2px 4px -1px rgba(23, 32, 51, 0.04)',
        'soft-lg': '0 10px 15px -3px rgba(23, 32, 51, 0.08), 0 4px 6px -2px rgba(23, 32, 51, 0.03)',
      }
    },
  },
  plugins: [],
}

