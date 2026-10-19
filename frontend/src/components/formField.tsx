import type { ComponentPropsWithoutRef, ReactNode } from 'react'

export type FormFieldProps = ComponentPropsWithoutRef<'input'> & {
  id: string
  label: ReactNode
  wrapperClassName?: string
  labelClassName?: string
}

export default function FormField({ id, label, wrapperClassName, labelClassName = '', className = '', ...props }: FormFieldProps) {
  return <div className={wrapperClassName}>
    <label htmlFor={id} className={`block text-sm font-medium text-gray-300 mb-1 ${labelClassName}`}>{label}</label>
    <input {...props} id={id} className={`w-full bg-zinc-800 border border-zinc-600 rounded-md py-2 px-3 focus:outline-none focus:ring-2 focus:ring-blue-500 ${className}`} />
  </div>
}
