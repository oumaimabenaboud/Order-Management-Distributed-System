import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { authGuard } from './auth.guard';

describe('authGuard', () => {
  let guard: authGuard;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [authGuard]
    });
    guard = TestBed.inject(authGuard);
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow access if user is logged in', () => {
    // Simulate user logged in by setting item in sessionStorage
    spyOn(sessionStorage, 'getItem').and.returnValue('someId');

    const canActivate = guard.canActivate(null!, null!);

    expect(canActivate).toBe(true);
  });

  it('should redirect to login page if user is not logged in', () => {
    // Simulate user not logged in by not setting item in sessionStorage
    spyOn(sessionStorage, 'getItem').and.returnValue(null);

    const canActivate = guard.canActivate(null!, null!);

    expect(canActivate).toEqual(jasmine.any(Object));
    expect(canActivate.toString()).toContain('/login');
  });
});
