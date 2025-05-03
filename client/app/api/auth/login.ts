import { NextResponse } from 'next/server';
import { authService } from './services';


export async function POST(request: Request) {
  const { email, password } = await request.json();
  
  try {
    const result = await authService.login(email, password);
    return NextResponse.json(result);
  } catch (error) {
    return NextResponse.json(
      { error: 'Authentication failed' },
      { status: 401 }
    );
  }
}