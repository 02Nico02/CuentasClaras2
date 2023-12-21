import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { HomeComponent } from './pages/home/home.component';
import { GrupoDetalleComponent } from './pages/grupo-detalle/grupo-detalle.component';

export const routes: Routes = [
    { path: "home", component: HomeComponent },
    { path: "login", component: LoginComponent },
    { path: "register", component: RegisterComponent },
    { path: "group-detalle", component: GrupoDetalleComponent },
    {path:"**",redirectTo:"login", pathMatch:"full"},

];
