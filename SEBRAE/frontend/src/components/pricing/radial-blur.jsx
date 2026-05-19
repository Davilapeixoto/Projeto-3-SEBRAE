import { cn } from "../../lib/utils"

export function RadialBlur({ className }) {
  return (
    <div
      className={cn(
        "pointer-events-none absolute top-0 left-0 isolate -z-10 h-1/2 w-full bg-[url('/radial-blur.png')] bg-cover bg-center bg-no-repeat md:bg-[url('/radial-blur.png')]",
        className
      )}
    />
  )
}