import { FeatureDetails } from "./feature-details"

export function FeatureCard({ feature, isActive }) {
  return (
    <div className="flex w-[320px] flex-col items-center gap-5 px-2 py-6">
      <FeatureDetails feature={feature} isActive={isActive} />

      <div className="bg-card w-full rounded-lg border p-8 pb-0">
        <img
          src={feature.image}
          alt="App Image"
          width={304}
          height={445}
        />
      </div>
    </div>
  )
}