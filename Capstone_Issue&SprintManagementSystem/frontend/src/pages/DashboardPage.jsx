// Dashboard: list of projects + create project.
import AppLayout from "../components/AppLayout";
import { getUsers } from "../services/users";
import { useAsyncData } from "../hooks/useAsyncData";

export default function DashboardPage() {
    const { data: users } = useAsyncData(getUsers);
    return (
        <AppLayout>
            <h1>Dashboard</h1>
            <p>Welcome to the dashboard!</p>
            <h2>Users</h2>
            <ul>
                {users?.map((user) => (
                    <li key={user.id}>{user.name}</li>
                ))}
            </ul>
        </AppLayout>
    );
}
