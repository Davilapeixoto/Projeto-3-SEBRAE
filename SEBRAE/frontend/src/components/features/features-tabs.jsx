import { FeatureDetails } from "./feature-details"
import {
  Tabs,
  TabsContent,
  TabsList,
  TabsTrigger,
} from "../ui/tabs"
import { cn } from "../../lib/utils"
import { useState } from "react"

export function FeaturesTabs({ features, className }) {
  const [activeTab, setActiveTab] = useState(features[0].title)

  return (
    <Tabs
      className={cn("w-full max-w-6xl", className)}
      value={activeTab}
      onValueChange={setActiveTab}
    >
      <TabsList className="grid h-auto w-full grid-cols-4 gap-6">
        {features.map((feature) => (
          <TabsTrigger
            key={feature.title}
            value={feature.title}
            className="flex cursor-pointer flex-col items-center justify-start gap-5 px-2 py-6"
          >
            <FeatureDetails
              feature={feature}
              isActive={activeTab === feature.title}
            />
          </TabsTrigger>
        ))}
      </TabsList>

      {features.map((feature) => (
        <TabsContent key={feature.title} value={feature.title}>
          <div className="bg-card flex w-full justify-center rounded-lg border p-8 pb-0">
            <img
              src={feature.image}
              alt="App Image"
              width={304}
              height={445}
            />
          </div>
        </TabsContent>
      ))}
    </Tabs>
  )
}