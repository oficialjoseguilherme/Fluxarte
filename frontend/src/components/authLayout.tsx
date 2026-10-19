import type { ReactNode } from 'react'

export default function AuthLayout({ children, width = 'login' }: { children: ReactNode; width?: 'login' | 'cadastro' }) {
  return <main className="flex min-h-screen items-center justify-center bg-zinc-950 p-6 text-white">
    <div className={`${width === 'login' ? 'max-w-md' : 'max-w-xl'} w-[448px]`}>{children}</div>
  </main>
}
