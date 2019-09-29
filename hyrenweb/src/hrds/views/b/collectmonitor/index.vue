<template>
<div>
    
</div>
</template>

<script>
import * as collect from '@/hrds/api/b/collectmonitor/index'
export default {
    data() {
        return {

        }
    },
    created() {
        collect.getAgentNumAndSourceNum().then((res) => {
            console.log(res)
        })
    }
}
</script>
