import { PlanSelect } from "./plan-select"
import { Button } from "../ui/button"
import { CheckIcon } from "lucide-react"
import { useState } from "react"

const features = [
  "Block scam texts with smart AI",
  "Remove your info from sites selling it",
  "Browse online securely with VPN",
  "Monitor your identity with timely alerts",
]

export function Plans() {
  const [selectedPriceId, setSelectedPriceId] = useState("monthly")

  return (
    <div className="relative z-10 mx-auto flex w-full max-w-md flex-col items-center">
      <h1 className="mt-8 mb-4 text-3xl font-bold md:text-center md:text-4xl">
        Unlock Scam Protection, VPN, and more
      </h1>

      <ul className="mt-4 mb-8 w-full space-y-3 md:mx-auto md:max-w-xs">
        {features.map((feature, index) => (
          <li
            key={index}
            className="text-foreground/60 flex items-center text-base"
          >
            <div className="bg-success mr-2 grid place-items-center rounded-full p-0.5">
              <CheckIcon className="size-4 p-0.5 text-white" />
            </div>

            {feature}
          </li>
        ))}
      </ul>

      <PlanSelect
        value={selectedPriceId}
        onChange={setSelectedPriceId}
      />

      <Button className="mb-8 w-full" size="lg" asChild>
        <a href={`/checkout?price_id=${selectedPriceId}`}>
          Checkout
        </a>
      </Button>

      <div className="text-muted-foreground flex justify-center gap-8 text-sm underline">
        <a href="/privacy-policy">Privacy Policy</a>
        <a href="/terms-and-conditions">Terms and Conditions</a>
        <a href="/refund-policy">Refund Policy</a>
      </div>
    </div>
  )
}