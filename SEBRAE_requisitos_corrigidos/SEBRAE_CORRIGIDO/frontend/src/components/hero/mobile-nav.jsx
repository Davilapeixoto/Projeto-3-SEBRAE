import {
  Drawer,
  DrawerContent,
  DrawerTrigger,
  DrawerTitle,
} from "../ui/drawer"
import { cn } from "../../lib/utils"
import { Menu } from "lucide-react"

export function MobileNav({ items, className }) {
  return (
    <nav
      className={cn(
        "flex w-full max-w-7xl items-center justify-between gap-4",
        className
      )}
    >
      <a href="/">
        <img src="/logo.svg" alt="logo" width={86} height={26} />
      </a>

      <Drawer direction="top">
        <DrawerTrigger className="relative -m-2 cursor-pointer p-2">
          <span className="sr-only">Open menu</span>
          <Menu className="h-6 w-6" />
        </DrawerTrigger>

        <DrawerContent className="flex flex-col gap-4 p-8">
          <DrawerTitle className="sr-only">Menu</DrawerTitle>

          {items.map((item) => (
            <a key={item.href} href={item.href}>
              {item.label}
            </a>
          ))}
        </DrawerContent>
      </Drawer>
    </nav>
  )
}