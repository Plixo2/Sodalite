dxc -T vs_6_0 -E main -spirv -fvk-use-dx-layout -fspv-target-env=vulkan1.1 vertex.hlsl -Fo spirv/vertex.spv
dxc -T ps_6_0 -E main -spirv -fvk-use-dx-layout -fspv-target-env=vulkan1.1 fragment.hlsl -Fo spirv/fragment.spv

dxc -T vs_6_0 -E main vertex.hlsl -Fo dxil/vertex.dxil
dxc -T ps_6_0 -E main fragment.hlsl -Fo dxil/fragment.dxil