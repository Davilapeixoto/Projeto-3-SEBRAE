import { FAQs } from "./faqs/faqs"
import { Features } from "./features/features"
import { Footer } from "./footer/footer"
import { Hero } from "./hero/hero"
import { Quote } from "./quote/quote"
import { Showcase } from "./showcase/showcase"
import { Testimonials } from "./testimonials/testimonials"
import { useRedirectWarning } from "../lib/redirect"

export default function VercelTemplate() {
  useRedirectWarning()

  return (
    <main>
      <Hero />
      <Showcase />
      <Quote />
      <Features />
      <Testimonials />
      <FAQs />
      <Footer />
    </main>
  )
}