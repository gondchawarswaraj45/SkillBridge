import { Stack } from 'expo-router';

export default function FreelancerLayout() {
  return (
    <Stack
      screenOptions={{
        headerStyle: { backgroundColor: '#FFFFFF' },
        headerTintColor: '#172033',
        headerTitleStyle: { fontWeight: '700' },
      }}
    >
      <Stack.Screen name="index" options={{ title: 'Freelancer Dashboard' }} />
    </Stack>
  );
}

