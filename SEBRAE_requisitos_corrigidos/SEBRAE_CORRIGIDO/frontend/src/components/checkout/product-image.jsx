import { cn } from "../../lib/utils"

export function ProductImage({ imageUrl, name, className }) {
  return (
    <img
      src={imageUrl}
      alt={name}
      className={cn("h-8.5 w-8.5 object-cover", className)}
    />
  )
}