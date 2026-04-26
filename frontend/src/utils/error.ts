import axios from "axios";

export function getErrorMessage(error: unknown, fallback: string): string {
  if (axios.isAxiosError(error)) {
    if (error.response?.data && typeof error.response.data === "string") {
      return error.response.data;
    }

    if (error.response?.status) {
      return `${fallback} Status: ${error.response.status}.`;
    }

    if (error.code === "ECONNABORTED") {
      return `${fallback} Tempo de resposta excedido.`;
    }

    if (error.message === "Network Error") {
      return `${fallback} Verifique se o backend está rodando.`;
    }
  }

  return fallback;
}