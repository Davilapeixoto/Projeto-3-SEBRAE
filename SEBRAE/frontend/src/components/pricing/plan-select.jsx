import {
  Choicebox,
  ChoiceboxItem,
  ChoiceboxItemContent,
  ChoiceboxItemHeader,
  ChoiceboxItemIndicator,
  ChoiceboxItemSubtitle,
  ChoiceboxItemTitle,
} from "../ui/choicebox"
import { Skeleton } from "../ui/skeleton"

const plans = [
  {
    priceId: "monthly",
    name: "Monthly",
    total: "$19",
    interval: "month",
    tag: "Popular",
  },
  {
    priceId: "yearly",
    name: "Yearly",
    total: "$190",
    interval: "year",
    tag: "Save 17%",
  },
]

function priceDescription(price) {
  return `Automatically renews at ${price.total} per ${price.interval}`
}

export function PlanSelect({ value, onChange }) {
  const loading = false

  const prices = {
    monthly: {
      name: "Monthly",
      total: "$19",
      interval: "month",
    },
    yearly: {
      name: "Yearly",
      total: "$190",
      interval: "year",
    },
  }

  if (loading) {
    return (
      <>
        <div className="mb-6 grid w-full grid-cols-2 justify-center gap-4">
          <Skeleton className="h-38 w-full" />
          <Skeleton className="h-38 w-full" />
        </div>

        <Skeleton className="mb-6 h-5 w-full" />
      </>
    )
  }

  return (
    <>
      <Choicebox
        className="mb-6 grid w-full grid-cols-2 justify-center gap-4"
        value={value}
        onValueChange={onChange}
      >
        {plans.map((plan) => (
          <ChoiceboxItem
            value={plan.priceId}
            key={plan.priceId}
            className="bg-card relative flex flex-col items-center rounded-lg px-8 py-6"
          >
            {plan.tag && (
              <span className="bg-accent border-accent-foreground absolute -top-4 left-0 rounded-full border-2 px-2 py-1 text-xs font-semibold text-white md:left-1/2 md:-translate-x-1/2">
                {plan.tag}
              </span>
            )}

            <ChoiceboxItemHeader className="w-full text-center">
              <ChoiceboxItemTitle>
                {prices[plan.priceId].name}
              </ChoiceboxItemTitle>

              <ChoiceboxItemSubtitle className="text-base">
                {prices[plan.priceId].total}
              </ChoiceboxItemSubtitle>
            </ChoiceboxItemHeader>

            <ChoiceboxItemContent>
              <ChoiceboxItemIndicator />
            </ChoiceboxItemContent>
          </ChoiceboxItem>
        ))}
      </Choicebox>

      {prices[value]?.interval && (
        <div className="text-muted-foreground mb-6 text-center text-base">
          {priceDescription(prices[value])}
        </div>
      )}
    </>
  )
}