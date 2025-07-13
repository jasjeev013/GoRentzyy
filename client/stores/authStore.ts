// stores/authStore.ts
import { create } from "zustand";
import { persist } from "zustand/middleware";
import { authService } from "../app/api/auth/services";
import { UserDto } from "../app/types";


interface AuthState {
  token: string | null;
  role: string | null;
  isAuthenticated: boolean;
  userData: UserDto | null;
  // expirationTime: number | null;
  login: (token: string, role: string) => void;
  logout: () => void;
  setUserData: (userData: UserDto) => void;
  updateUserData: (
    userData: Partial<UserDto>,
    image: File | null
  ) => Promise<void>;
  // scheduleTokenExpirationCheck: (expirationTime: number) => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      token: null,
      role: null,
      isAuthenticated: false,
      userData: null,
      expirationTime: null,
      login: (token, role) => {
        // const payload = JSON.parse(atob(token.split(".")[1]));
        // const expirationTime = payload.exp * 1000;

        // get().scheduleTokenExpirationCheck(expirationTime);

        set({
          token,
          role,
          isAuthenticated: true,
          // expirationTime,
        });
        localStorage.setItem("token", token);
      },
      logout: () => {
        set({
          token: null,
          role: null,
          isAuthenticated: false,
          userData: null,
          // expirationTime: null,
        });
        localStorage.removeItem("token");
      },
      setUserData: (userData) => set({ userData }),
      updateUserData: async (userData, image) => {
        const { token } = get();
        if (!token) throw new Error("Not authenticated");

        try {
          const updatedUser = await authService.updateUserData(
            userData,
            image,
            token
          );
          set({ userData: updatedUser.object });
        } catch (error) {
          console.error("Failed to update user data:", error);
          throw error;
        }
      },
      /*scheduleTokenExpirationCheck: (expirationTime) => {
        const expDate = new Date(expirationTime);
        const presentDate = new Date();
        console.log(expDate <= presentDate);
        console.log("Expiration:", expDate.toISOString());
        console.log("Now:", presentDate.toISOString());

        if (expDate <= presentDate) {
          // Token is already expired
          get().logout();
          window.location.href = "/login";
        }
      },*/
    }),
    {
      name: "auth-storage",
    }
  )
);
