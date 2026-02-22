import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from '@tailwindcss/postcss'
import autoprefixer from 'autoprefixer'
import { resolve } from 'path'
import fs from 'node:fs'

const certDir = resolve(__dirname, '../certs')
const httpsOpt = fs.existsSync(resolve(certDir, 'cert.pem'))
  ? {
    key: fs.readFileSync(resolve(certDir, 'key.pem')),
    cert: fs.readFileSync(resolve(certDir, 'cert.pem'))
  }
  : undefined

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    https: httpsOpt
  },
  css: {
    postcss: {
      plugins: [
        tailwindcss(),
        autoprefixer()
      ]
    }
  }
})
