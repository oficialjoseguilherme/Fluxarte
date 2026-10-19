import type { Meta, StoryObj } from '@storybook/react-vite';
import { Route, Routes } from 'react-router';
import CatalogoLayout from '../components/catalogoLayout';
import { CatalogoSkeleton } from '../components/catalogoSkeleton';
import { authStore } from '../stores/authStore';
import { dispatcher } from '../dispatchers/dispatcher';
const meta = {
  component: CatalogoLayout, tags: ['autodocs'], parameters: { layout: 'fullscreen' },
  beforeEach: () => {
    const previous = authStore.getSnapshot();
    dispatcher.dispatch({ type: 'LOGIN_SUCESSO', tokens: { accessToken: 'storybook-preview', refreshToken: 'storybook-preview', tipo: 'Bearer' } });
    return () => {
      dispatcher.dispatch(previous.tokens
        ? { type: 'LOGIN_SUCESSO', tokens: previous.tokens }
        : { type: 'LOGIN_FALHOU', mensagem: previous.erro ?? '' });
    };
  },
  render: () => <Routes><Route element={<CatalogoLayout />}><Route path="*" element={<CatalogoSkeleton />} /></Route></Routes>,
} satisfies Meta<typeof CatalogoLayout>;
export default meta;
type Story = StoryObj<typeof meta>;
export const Padrao: Story = {};