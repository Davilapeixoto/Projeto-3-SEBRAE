import { cn } from "../../lib/utils"

export function FooterBlur({ className }) {
  return (
    <div
      className={cn(
        "pointer-events-none absolute bottom-0 left-0 -z-10 h-full w-full bg-[url('/footer-blur-mobile.png')] mask-t-from-90% bg-cover bg-center bg-no-repeat md:bg-[url('/footer-blur-desktop.png')]",
        className
      )}
    />
  )
}