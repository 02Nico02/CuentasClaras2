import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { HomeComponent } from './pages/home/home.component';
import { GrupoDetalleComponent } from './pages/grupo-detalle/grupo-detalle.component';
import { CrearGrupoComponent } from './pages/crear-grupo/crear-grupo.component';

export const routes: Routes = [
    { path: "", component: HomeComponent },
    { path: "login", component: LoginComponent },
    { path: "register", component: RegisterComponent },
    { path: "grupo/:id/detalle", component: GrupoDetalleComponent },
    { path: "crear-grupo", component: CrearGrupoComponent },
    // {path:"**",redirectTo:"login", pathMatch:"full"},

];
