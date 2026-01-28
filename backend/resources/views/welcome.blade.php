<!DOCTYPE html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}" class="scroll-smooth">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>FloodRescue Admin Portal</title>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Outlet:wght@300;400;500;600;700&family=Inter:wght@300;400;500;600&display=swap" rel="stylesheet">
        
        <!-- Use CDN for reliable styling without local build -->
        <script src="https://cdn.tailwindcss.com"></script>
        <script>
            tailwind.config = {
                theme: {
                    extend: {
                        fontFamily: {
                            sans: ['Inter', 'sans-serif'],
                            display: ['Outlet', 'sans-serif'],
                        }
                    }
                }
            }
        </script>

        <style>
            body { font-family: 'Inter', sans-serif; }
            h1, h2, h3 { font-family: 'Outfit', sans-serif; }
            .glass {
                background: rgba(255, 255, 255, 0.05);
                backdrop-filter: blur(10px);
                border: 1px solid rgba(255, 255, 255, 0.1);
            }
            .animate-fade-in-up {
                animation: fadeInUp 0.8s ease-out;
            }
            @keyframes fadeInUp {
                from { opacity: 0; transform: translateY(20px); }
                to { opacity: 1; transform: translateY(0); }
            }
        </style>
    </head>
    <body class="bg-[#0F172A] text-white selection:bg-red-500 selection:text-white overflow-x-hidden antialiased">
        
        <!-- Background Gradients -->
        <div class="fixed inset-0 z-0 pointer-events-none">
            <div class="absolute top-[-20%] right-[-10%] w-[600px] h-[600px] bg-red-600/20 rounded-full blur-[120px]"></div>
            <div class="absolute bottom-[-20%] left-[-10%] w-[600px] h-[600px] bg-blue-600/10 rounded-full blur-[120px]"></div>
        </div>

        <div class="relative z-10 flex flex-col min-h-screen">
            <!-- Navigation -->
            <nav class="w-full px-6 py-6 flex justify-between items-center max-w-7xl mx-auto w-full">
                <div class="flex items-center gap-2">
                    <div class="w-8 h-8 bg-gradient-to-br from-red-500 to-orange-600 rounded-lg flex items-center justify-center shadow-lg shadow-red-500/30">
                        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5 text-white">
                            <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v3.75m-9.303 3.376c-.866 1.5.217 3.374 1.948 3.374h14.71c1.73 0 2.813-1.874 1.948-3.374L13.949 3.378c-.866-1.5-3.032-1.5-3.898 0L2.697 16.126ZM12 15.75h.007v.008H12v-.008Z" />
                        </svg>
                    </div>
                    <span class="text-2xl font-bold tracking-tight text-white">Flood<span class="text-red-500">Rescue</span></span>
                </div>
                <!-- Auth Actions -->
                <div class="flex items-center gap-4">
                     <a href="{{ url('/admin/login') }}" class="px-5 py-2.5 rounded-full bg-white/10 hover:bg-white/20 transition duration-300 font-medium text-sm backdrop-blur-md border border-white/5">
                        Admin Portal
                    </a>
                </div>
            </nav>

            <!-- Hero Section -->
            <main class="flex-grow flex flex-col items-center justify-center text-center px-4 max-w-5xl mx-auto mt-10 md:mt-20">
                <div class="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-red-500/10 border border-red-500/20 text-red-400 text-xs font-semibold tracking-wide uppercase mb-6 animate-fade-in-up">
                    <span class="w-2 h-2 rounded-full bg-red-500 animate-pulse"></span>
                    Emergency Response System
                </div>
                
                <h1 class="text-5xl md:text-7xl font-bold text-white mb-6 leading-tight tracking-tight drop-shadow-2xl animate-fade-in-up" style="animation-delay: 0.1s">
                    Rapid Coordination for <br>
                    <span class="text-transparent bg-clip-text bg-gradient-to-r from-red-400 to-orange-500">Flood Disasters</span>
                </h1>
                
                <p class="text-lg md:text-xl text-gray-400 max-w-2xl mb-10 leading-relaxed animate-fade-in-up" style="animation-delay: 0.2s">
                    A centralized command center for monitoring real-time flood reports, managing safe shelters, and coordinating rescue teams efficiently.
                </p>
                
                <div class="flex flex-col sm:flex-row gap-4 w-full justify-center animate-fade-in-up" style="animation-delay: 0.3s">
                    <a href="{{ url('/admin/login') }}" class="group relative px-8 py-4 bg-gradient-to-r from-red-600 to-red-500 rounded-xl font-semibold text-white shadow-xl shadow-red-500/20 hover:shadow-red-500/40 hover:-translate-y-0.5 transition-all duration-300 overflow-hidden">
                        <div class="absolute inset-0 bg-white/20 group-hover:translate-x-full transition-transform duration-500 ease-out skew-x-12 -translate-x-full"></div>
                        <span class="relative flex items-center gap-2">
                            Access Command Center
                            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-4 h-4">
                                <path stroke-linecap="round" stroke-linejoin="round" d="M13.5 4.5 21 12m0 0-7.5 7.5M21 12H3" />
                            </svg>
                        </span>
                    </a>
                </div>

                <!-- Stats / Features -->
                <div class="grid grid-cols-1 md:grid-cols-3 gap-6 w-full mt-24 mb-16 animate-fade-in-up" style="animation-delay: 0.4s">
                    <div class="glass p-6 rounded-2xl text-left hover:bg-white/10 transition duration-300 group">
                        <div class="w-12 h-12 bg-red-500/20 rounded-lg flex items-center justify-center mb-4 text-red-400 group-hover:scale-110 transition duration-300">
                             <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
                                <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v3.75m9-.75a9 9 0 1 1-18 0 9 9 0 0 1 18 0Zm-9 3.75h.008v.008H12v-.008Z" />
                            </svg>
                        </div>
                        <h3 class="text-xl font-semibold text-white mb-2">Real-time alerts</h3>
                        <p class="text-gray-400 text-sm">Instant notifications from mobile users directly to your command dashboard.</p>
                    </div>
                    
                    <div class="glass p-6 rounded-2xl text-left hover:bg-white/10 transition duration-300 group">
                        <div class="w-12 h-12 bg-blue-500/20 rounded-lg flex items-center justify-center mb-4 text-blue-400 group-hover:scale-110 transition duration-300">
                            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
                                <path stroke-linecap="round" stroke-linejoin="round" d="M15 10.5a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z" />
                                <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 10.5c0 7.142-7.5 11.25-7.5 11.25S4.5 17.642 4.5 10.5a7.5 7.5 0 1 1 15 0Z" />
                            </svg>
                        </div>
                        <h3 class="text-xl font-semibold text-white mb-2">Live Mapping</h3>
                        <p class="text-gray-400 text-sm">Visual markers for floods, blocked roads, and safe shelters on a dynamic map.</p>
                    </div>

                    <div class="glass p-6 rounded-2xl text-left hover:bg-white/10 transition duration-300 group">
                        <div class="w-12 h-12 bg-green-500/20 rounded-lg flex items-center justify-center mb-4 text-green-400 group-hover:scale-110 transition duration-300">
                           <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
                              <path stroke-linecap="round" stroke-linejoin="round" d="M9 12.75 11.25 15 15 9.75M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z" />
                            </svg>
                        </div>
                        <h3 class="text-xl font-semibold text-white mb-2">Verified Data</h3>
                        <p class="text-gray-400 text-sm">Reliable data management for critical decision making during disasters.</p>
                    </div>
                </div>
            </main>

            <footer class="py-8 text-center text-gray-500 text-sm border-t border-white/5">
                &copy; {{ date('Y') }} FloodRescue System. Authorized Personnel Only.
            </footer>
        </div>
    </body>
</html>
