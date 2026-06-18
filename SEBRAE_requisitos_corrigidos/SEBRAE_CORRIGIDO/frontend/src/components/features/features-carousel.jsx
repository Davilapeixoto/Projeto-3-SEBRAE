import { FeatureCard } from "./feature-card"
import {
  Carousel,
  CarouselContent,
  CarouselItem,
} from "../ui/carousel"
import { cn } from "../../lib/utils"
import { useState } from "react"

export function FeaturesCarousel({ features, className }) {
  const [carouselApi, setCarouselApi] = useState(null)
  const [current, setCurrent] = useState(1)

  return (
    <div className={cn("w-full", className)}>
      <Carousel setApi={setCarouselApi}>
        <CarouselContent>
          {features.map((feature, index) => (
            <CarouselItem
              key={feature.title}
              className="basis-60"
              onClick={() => {
                if (carouselApi) {
                  carouselApi.scrollTo(index)
                }

                setCurrent(index + 1)
              }}
            >
              <FeatureCard
                feature={feature}
                isActive={current === index + 1}
              />
            </CarouselItem>
          ))}
        </CarouselContent>
      </Carousel>
    </div>
  )
}