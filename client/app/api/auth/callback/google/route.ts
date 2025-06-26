
import { NextResponse } from "next/server";
import { authService } from "../../services";
import { log } from "console";

export async function GET(request: Request) {


  const { searchParams } = new URL(request.url);
  const code = searchParams.get("code");


  if (!code) {
    return NextResponse.redirect(
      new URL("/login?error=missing_code", request.url)
    );
  }


  try {
    // Use your existing service
  console.log("Google OAuth callback received 4");

    const { jwtToken, role, status } = await authService.exchangeCodeForToken(code);

    if (status !== "Authentication successful") {
      throw new Error("Authentication failed");
    }
 
    // Redirect with token and role as query params
    const redirectUrl = new URL("/auth/callback", request.url);
    redirectUrl.searchParams.set("token", jwtToken);
    redirectUrl.searchParams.set("role", role);
   
    return NextResponse.redirect(redirectUrl);
  } catch (error) {
    return NextResponse.redirect(
      new URL(`/login?error=${encodeURIComponent(error.message)}`, request.url)
    );
  }
}
