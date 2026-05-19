import { Button } from "../ui/button"
import {
  NavigationMenu,
  NavigationMenuItem,
  NavigationMenuLink,
  NavigationMenuList,
} from "../ui/navigation-menu"
import { cn } from "../../lib/utils"

export function DesktopNav({ items, className }) {
  return (
    <nav
      className={cn(
        "mx-auto flex w-full max-w-7xl items-center justify-between gap-4",
        className
      )}
    >
      <a href="/">
        <img src="/logo.svg" alt="logo" width={86} height={26} />
      </a>

      <NavigationMenu>
        <NavigationMenuList className="gap-8">
          {items.map((item) => (
            <NavigationMenuItem key={item.href}>
              <NavigationMenuLink href={item.href}>
                {item.label}
              </NavigationMenuLink>
            </NavigationMenuItem>
          ))}
        </NavigationMenuList>
      </NavigationMenu>

      <Button asChild>
        <a href="/pricing">Get Started</a>
      </Button>
    </nav>
  )
}