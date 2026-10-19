import type { ComponentPropsWithoutRef, ReactNode } from 'react'
import { Link } from 'react-router'
import type { LinkProps } from 'react-router'

type Appearance = {
  children: ReactNode
  variant?: 'primary' | 'light' | 'secondary'
  size?: 'form' | 'action' | 'featured'
  fullWidth?: boolean
}

type NativeButton = ComponentPropsWithoutRef<'button'> & { href?: never; to?: never }
type ExternalLink = Omit<ComponentPropsWithoutRef<'a'>, 'href' | 'type'> & {
  href: string; to?: never; type?: never; disabled?: never
}
type InternalLink = Omit<LinkProps, 'type'> & {
  to: LinkProps['to']; href?: never; type?: never; disabled?: never
}

export type ButtonProps = Appearance & (NativeButton | ExternalLink | InternalLink)

const variants = {
  primary: 'bg-red-600 text-white hover:bg-red-700',
  light: 'bg-white text-black',
  secondary: 'bg-[#333] text-white',
}
const sizes = {
  form: 'rounded-md px-4 py-2 disabled:cursor-not-allowed disabled:opacity-50',
  action: 'inline-block rounded px-5 py-3',
  featured: 'inline-flex items-center gap-2 rounded-[5px] px-[25px] py-3 text-xl hover:opacity-70 max-[760px]:text-base',
}

export default function Button({ children, variant = 'primary', size = 'action', fullWidth = false, className = '', ...props }: ButtonProps) {
  const classes = `font-bold ${variants[variant]} ${sizes[size]} ${fullWidth ? 'w-full' : ''} ${className}`
  if (props.to !== undefined) return <Link {...props} className={classes}>{children}</Link>
  if (props.href !== undefined) return <a {...props} className={classes}>{children}</a>
  return <button {...props} type={props.type ?? 'button'} className={classes}>{children}</button>
}
