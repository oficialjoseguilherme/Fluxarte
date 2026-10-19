import type { ComponentPropsWithoutRef } from 'react'

export type IconButtonProps = Omit<ComponentPropsWithoutRef<'button'>, 'aria-label'> & { label: string }

export default function IconButton({ label, children, className = '', type = 'button', ...props }: IconButtonProps) {
  return <button {...props} type={type} aria-label={label} className={`flex items-center border-0 bg-transparent p-[6px] hover:opacity-80 ${className}`}>{children}</button>
}
