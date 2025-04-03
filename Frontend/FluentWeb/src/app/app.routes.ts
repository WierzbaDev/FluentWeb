import {Routes} from '@angular/router';
import {LoginComponent} from './auth/pages/login/login.component';
import {RegisterComponent} from './auth/pages/register/register.component';
import {HomeComponent} from './pages/home/home.component';
import {DashboardComponent} from './pages/dashboard/dashboard.component';
import {LessonComponent} from './pages/lesson/lesson.component';
import {ReviewComponent} from './pages/review/review.component';
import {RankingComponent} from './pages/ranking/ranking.component';
import {SettingComponent} from './pages/setting/setting.component';
import {AdminUsersComponent} from './pages/admin-users/admin-users.component';
import {AdminWordsComponent} from './pages/admin-words/admin-words.component';
import {AdminSettingComponent} from './pages/admin-setting/admin-setting.component';
import {RoleGuard} from './guards/role.guard';
import {UnauthorizedComponent} from './pages/unauthorized/unauthorized.component';
import {ForgotPasswordComponent} from './pages/forgot-password/forgot-password.component';
import {ResetPasswordComponent} from './pages/reset-password/reset-password.component';
import {VerifyAccountComponent} from './pages/verify-account/verify-account.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'auth/login', component: LoginComponent },
  { path: 'auth/register', component: RegisterComponent },
  { path: 'dashboard',
    component: DashboardComponent,
    canActivate: [RoleGuard],
    data: { expectedRoles: ['USER'] }
  },
  { path: 'lesson',
    component: LessonComponent,
    canActivate: [RoleGuard],
    data: { expectedRoles: ['USER'] } },

  { path: 'repeat',
    component: ReviewComponent,
    canActivate: [RoleGuard],
    data: { expectedRoles: ['USER'] } },

  { path: 'ranking',
    component: RankingComponent,
    canActivate: [RoleGuard],
    data: { expectedRoles: ['USER'] } },

  { path: 'setting',
    component: SettingComponent,
    canActivate: [RoleGuard],
    data: { expectedRoles: ['USER'] } },

  { path: 'admin/users',
    component: AdminUsersComponent,
    canActivate: [RoleGuard],
    data: { expectedRoles: ['ADMIN'] } },

  { path: 'admin/words',
    component: AdminWordsComponent,
    canActivate: [RoleGuard],
    data: { expectedRoles: ['ADMIN'] } },

  { path: 'admin/setting',
    component: AdminSettingComponent,
    canActivate: [RoleGuard],
    data: { expectedRoles: ['ADMIN'] } },

  { path: 'unauthorized', component: UnauthorizedComponent },

  { path: 'forgot-password', component: ForgotPasswordComponent },

  {path: 'reset-password/:code',
    component: ResetPasswordComponent,
    data: {
      renderMode: 'client-only'
    }
  },

  { path: 'verify/:uuid',
    component: VerifyAccountComponent,
    data: { skipPrerender: true }
  },

  { path: '**', redirectTo: 'auth/login' }
];
